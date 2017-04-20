package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.DefaultErrorMessage;
import models.Electronic;
import play.db.Database;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;

public class ElectronicController extends Controller{

    private Database db;

    @Inject
    public ElectronicController(Database db) {
        this.db = db;
    }

    public Result insertElectronic(){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        Electronic electronic = new Electronic(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"electronic",json.findPath("producer").textValue(),json.findPath("model").textValue());
        //Properties checker
        return insertElectronic(electronic);
    }


    public Result insertElectronic(Electronic electronic){
        try (
            Connection connection = db.getConnection();
            PreparedStatement articleStatement = connection.prepareStatement("INSERT INTO articles (name, description, condition, price, creationdate, image) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        ){
            articleStatement.setString(1,electronic.getName());
            articleStatement.setString(2,electronic.getDescription());
            articleStatement.setInt(3,electronic.getCondition());
            articleStatement.setInt(4,electronic.getPrice());
            articleStatement.setDate(5,electronic.getCreationDate());
            articleStatement.setString(6,electronic.getImage());

            int affectedRows = articleStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating electronic failed, no rows affected.");
            }

            try (
                ResultSet articleGeneratedKeys = articleStatement.getGeneratedKeys();
                PreparedStatement electronicStatement = connection.prepareStatement("INSERT INTO electronics (electronic_id, manufacturer, modell) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ) {
                if (articleGeneratedKeys.next()) {
                    electronicStatement.setInt(1,articleGeneratedKeys.getInt(1));
                    electronicStatement.setString(2,electronic.getProducer());
                    electronicStatement.setString(3,electronic.getModel());
                    electronicStatement.executeUpdate();

                    electronic.setId(articleGeneratedKeys.getInt(1));

                }
                else {
                    throw new SQLException("Creating electronic failed, no ID obtained.");
                }
            }
        }catch (SQLException e){
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        }
        return ok(Json.toJson(electronic));
    }


    public Result getAllElectronics(){
        try (Connection connection = db.getConnection()){

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN electronics on articles.article_id = electronics.electronic_id;").executeQuery();
            ArrayList<Electronic> list = new ArrayList<>();

            while(resultSet.next()){
                Electronic electronic = new Electronic(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"electronic",resultSet.getString("manufacturer"),resultSet.getString("modell"));
                electronic.setId(resultSet.getInt("article_id"));
                list.add(electronic);
            }

            return ok(Json.toJson(list));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }
    }


    public Result getOneElectronic(Integer id){

        try (Connection connection = db.getConnection()){

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN electronics on articles.article_id = electronics.electronic_id WHERE article_id ="+id+"").executeQuery();

            if(resultSet.next()){
                Electronic electronic = new Electronic(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"electronic",resultSet.getString("manufacturer"),resultSet.getString("modell"));
                electronic.setId(resultSet.getInt("article_id"));
                return ok(Json.toJson(electronic));
            }

            return badRequest(Json.toJson(new DefaultErrorMessage(14,"No electronic with given ID found")));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }
    }


}
