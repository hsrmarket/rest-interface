package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.DefaultErrorMessage;
import models.OtherArticle;
import play.db.Database;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;

public class OtherArticleController extends Controller {

    private Database db;

    @Inject
    public OtherArticleController(Database db) {
        this.db = db;
    }


    public Result insertOtherArticle(){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        OtherArticle otherArticle = new OtherArticle(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"other");
        //Properties checker
        return insertOtherArticle(otherArticle);
    }


    public Result insertOtherArticle(OtherArticle otherArticle){

        try (
            Connection connection = db.getConnection();
            PreparedStatement articleStatement = connection.prepareStatement("INSERT INTO articles (name, description, condition, price, creationdate, image) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        ){
            articleStatement.setString(1,otherArticle.getName());
            articleStatement.setString(2,otherArticle.getDescription());
            articleStatement.setInt(3,otherArticle.getCondition());
            articleStatement.setInt(4,otherArticle.getPrice());
            articleStatement.setDate(5,otherArticle.getCreationDate());
            articleStatement.setString(6,otherArticle.getImage());

            int affectedRows = articleStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating 'other article' failed, no rows affected.");
            }

            try (
                    ResultSet articleGeneratedKeys = articleStatement.getGeneratedKeys();
                    PreparedStatement otherArticleStatement = connection.prepareStatement("INSERT INTO otherarticles (otherarticle_id) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            ) {
                if (articleGeneratedKeys.next()) {
                    otherArticleStatement.setInt(1,articleGeneratedKeys.getInt(1));
                    otherArticleStatement.executeUpdate();

                    otherArticle.setId(articleGeneratedKeys.getInt(1));

                }
                else {
                    throw new SQLException("Creating 'other article' failed, no ID obtained.");
                }
            }
        }catch (SQLException e){
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        }
        return ok(Json.toJson(otherArticle));
    }


    public Result updateOneOtherArticle(Integer id){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        if(id == null){
            return badRequest(Json.toJson(new DefaultErrorMessage(12,"Missing Parameter (ID)")));
        }

        OtherArticle otherArticle = new OtherArticle(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"other");
        //Properties checker
        otherArticle.setId(json.findPath("id").intValue());
        return updateOneOtherArticle(otherArticle);
    }


    public Result updateOneOtherArticle(OtherArticle otherArticle){
        try (
                Connection connection = db.getConnection();
                PreparedStatement articleStatement = connection.prepareStatement("UPDATE articles SET name = ?, description = ?, condition = ?, price = ?, creationdate = ?, image = ? WHERE article_id = ?", Statement.RETURN_GENERATED_KEYS);
        ){
            articleStatement.setString(1,otherArticle.getName());
            articleStatement.setString(2,otherArticle.getDescription());
            articleStatement.setInt(3,otherArticle.getCondition());
            articleStatement.setInt(4,otherArticle.getPrice());
            articleStatement.setDate(5,otherArticle.getCreationDate());
            articleStatement.setString(6,otherArticle.getImage());
            articleStatement.setInt(7,otherArticle.getId());

            int affectedRows = articleStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating 'other article' failed, no rows affected.");
            }

        }catch (SQLException e){
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        }
        return ok(Json.toJson(otherArticle));
    }


    public Result getAllOtherArticles(){

        try (Connection connection = db.getConnection()){

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN otherarticles on articles.article_id = otherarticles.otherarticle_id").executeQuery();
            ArrayList<OtherArticle> list = new ArrayList<>();

            while(resultSet.next()){
                OtherArticle otherArticle = new OtherArticle(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"other");
                otherArticle.setId(resultSet.getInt("article_id"));
                list.add(otherArticle);
            }

            return ok(Json.toJson(list));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }
    }


    public Result getOneOtherArticle(Integer id){

        try (Connection connection = db.getConnection()){

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN otherarticles on articles.article_id = otherarticles.otherarticle_id WHERE article_id ="+id+"").executeQuery();

            if(resultSet.next()){
                OtherArticle otherArticle = new OtherArticle(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"other");
                otherArticle.setId(resultSet.getInt("article_id"));
                return ok(Json.toJson(otherArticle));
            }

            return badRequest(Json.toJson(new DefaultErrorMessage(14,"No other article with given ID found")));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }

    }

}
