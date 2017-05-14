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
    private Connection connection;

    @Inject
    public OtherArticleController(Database db) {
        this.db = db;
    }


    public Result insertOtherArticle(){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        OtherArticle otherArticle = new OtherArticle(json.findPath("name").textValue(),json.findPath("price").doubleValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"other");
        //Properties checker
        int account_id = json.findPath("createdby").intValue();

        try {
            return ok(Json.toJson(insertOtherArticle(otherArticle, account_id)));
        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
        }
    }


    private OtherArticle insertOtherArticle(OtherArticle otherArticle, int account_id) throws SQLException{

        connection = db.getConnection();
        PreparedStatement articleStatement = connection.prepareStatement("INSERT INTO articles (name, description, condition, price, creationdate, image) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

        articleStatement.setString(1,otherArticle.getName());
        articleStatement.setString(2,otherArticle.getDescription());
        articleStatement.setInt(3,otherArticle.getCondition());
        articleStatement.setDouble(4,otherArticle.getPrice());
        articleStatement.setDate(5,otherArticle.getCreationDate());
        articleStatement.setString(6,otherArticle.getImage());

        int affectedRows = articleStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating 'other article' failed, no rows affected.");
        }

        ResultSet articleGeneratedKeys = articleStatement.getGeneratedKeys();
        PreparedStatement otherArticleStatement = connection.prepareStatement("INSERT INTO otherarticles (otherarticle_id) VALUES (?)", Statement.RETURN_GENERATED_KEYS);

        if (articleGeneratedKeys.next()) {
            PreparedStatement allocationStatement = connection.prepareStatement("INSERT INTO articleaccountallocation (account_id, article_id) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            allocationStatement.setInt(1,account_id);
            allocationStatement.setInt(2,articleGeneratedKeys.getInt(1));
            allocationStatement.executeUpdate();

            otherArticleStatement.setInt(1,articleGeneratedKeys.getInt(1));
            otherArticleStatement.executeUpdate();

            otherArticle.setId(articleGeneratedKeys.getInt(1));

        }
        else {
            throw new SQLException("Creating 'other article' failed, no ID obtained.");
        }

        return otherArticle;
    }


    public Result updateOneOtherArticle(Integer id){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        if(id == null){
            return badRequest(Json.toJson(new DefaultErrorMessage(12,"Missing Parameter (ID)")));
        }

        OtherArticle otherArticle = new OtherArticle(json.findPath("name").textValue(),json.findPath("price").doubleValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"other");
        //Properties checker
        otherArticle.setId(id);
        try {
            return ok(Json.toJson(updateOneOtherArticle(otherArticle)));
        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
        }
    }


    private OtherArticle updateOneOtherArticle(OtherArticle otherArticle) throws SQLException{

        connection = db.getConnection();
        PreparedStatement articleStatement = connection.prepareStatement("UPDATE articles SET name = ?, description = ?, condition = ?, price = ?, creationdate = ?, image = ? WHERE article_id = ?", Statement.RETURN_GENERATED_KEYS);

        articleStatement.setString(1,otherArticle.getName());
        articleStatement.setString(2,otherArticle.getDescription());
        articleStatement.setInt(3,otherArticle.getCondition());
        articleStatement.setDouble(4,otherArticle.getPrice());
        articleStatement.setDate(5,otherArticle.getCreationDate());
        articleStatement.setString(6,otherArticle.getImage());
        articleStatement.setInt(7,otherArticle.getId());

        int affectedRows = articleStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Updating 'other article' failed, no rows affected.");
        }

        return otherArticle;
    }


    public Result getAllOtherArticles(){

        try {
            connection = db.getConnection();

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN otherarticles on articles.article_id = otherarticles.otherarticle_id LEFT JOIN purchase on articles.article_id  = purchase.article_id WHERE purchase.purchase_id IS NULL;").executeQuery();
            ArrayList<OtherArticle> list = new ArrayList<>();

            while(resultSet.next()){
                OtherArticle otherArticle = new OtherArticle(resultSet.getString("name"),resultSet.getDouble("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"other");
                otherArticle.setId(resultSet.getInt("article_id"));
                list.add(otherArticle);
            }

            return ok(Json.toJson(list));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
        }
    }


    public Result getOneOtherArticle(Integer id){
        try {
            return ok(Json.toJson(getOneRawOtherArticle(id)));
        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
        }
    }


    public OtherArticle getOneRawOtherArticle(Integer id) throws SQLException{

        connection = db.getConnection();
        ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN otherarticles on articles.article_id = otherarticles.otherarticle_id WHERE article_id ="+id+"").executeQuery();

        if(resultSet.next()){
            OtherArticle otherArticle = new OtherArticle(resultSet.getString("name"),resultSet.getDouble("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"other");
            otherArticle.setId(resultSet.getInt("article_id"));

            connection.close();
            return otherArticle;
        }

        connection.close();/**/
        throw new SQLException("No other article with given ID found");

    }

}
