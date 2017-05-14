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
    private Connection connection;

    @Inject
    public ElectronicController(Database db) {
        this.db = db;
    }


    public Result insertElectronic(){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        Electronic electronic = new Electronic(json.findPath("name").textValue(),json.findPath("price").doubleValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"electronic",json.findPath("producer").textValue(),json.findPath("model").textValue());
        //Properties checker
        int account_id = json.findPath("createdby").intValue();

        try {
            return ok(Json.toJson(insertElectronic(electronic, account_id)));
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


    private Electronic insertElectronic(Electronic electronic, int account_id) throws SQLException{

        connection = db.getConnection();
        PreparedStatement articleStatement = connection.prepareStatement("INSERT INTO articles (name, description, condition, price, creationdate, image) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

        articleStatement.setString(1,electronic.getName());
        articleStatement.setString(2,electronic.getDescription());
        articleStatement.setInt(3,electronic.getCondition());
        articleStatement.setDouble(4,electronic.getPrice());
        articleStatement.setDate(5,electronic.getCreationDate());
        articleStatement.setString(6,electronic.getImage());

        int affectedRows = articleStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating electronic failed, no rows affected.");
        }


        ResultSet articleGeneratedKeys = articleStatement.getGeneratedKeys();
        PreparedStatement electronicStatement = connection.prepareStatement("INSERT INTO electronics (electronic_id, manufacturer, modell) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);

        if (articleGeneratedKeys.next()) {
            PreparedStatement allocationStatement = connection.prepareStatement("INSERT INTO articleaccountallocation (account_id, article_id) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            allocationStatement.setInt(1,account_id);
            allocationStatement.setInt(2,articleGeneratedKeys.getInt(1));
            allocationStatement.executeUpdate();

            electronicStatement.setInt(1,articleGeneratedKeys.getInt(1));
            electronicStatement.setString(2,electronic.getProducer());
            electronicStatement.setString(3,electronic.getModel());
            electronicStatement.executeUpdate();

            electronic.setId(articleGeneratedKeys.getInt(1));

        } else {
            throw new SQLException("Creating electronic failed, no ID obtained.");
        }

        return electronic;
    }


    public Result updateOneElectronic(Integer id){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        if(id == null){
            return badRequest(Json.toJson(new DefaultErrorMessage(12,"Missing Parameter (ID)")));
        }

        Electronic electronic = new Electronic(json.findPath("name").textValue(),json.findPath("price").doubleValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"electronic",json.findPath("producer").textValue(),json.findPath("model").textValue());
        //Properties checker
        electronic.setId(id);
        try {
            return ok(Json.toJson(updateOneElectronic(electronic)));
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


    private Electronic updateOneElectronic(Electronic electronic) throws SQLException{

        connection = db.getConnection();
        PreparedStatement articleStatement = connection.prepareStatement("UPDATE articles SET name = ?, description = ?, condition = ?, price = ?, creationdate = ?, image = ? WHERE article_id = ?", Statement.RETURN_GENERATED_KEYS);

        articleStatement.setString(1,electronic.getName());
        articleStatement.setString(2,electronic.getDescription());
        articleStatement.setInt(3,electronic.getCondition());
        articleStatement.setDouble(4,electronic.getPrice());
        articleStatement.setDate(5,electronic.getCreationDate());
        articleStatement.setString(6,electronic.getImage());
        articleStatement.setInt(7,electronic.getId());

        int affectedRows = articleStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Updating electronic failed, no rows affected.");
        }


        ResultSet articleGeneratedKeys = articleStatement.getGeneratedKeys();
        PreparedStatement electronicStatement = connection.prepareStatement("UPDATE electronics SET manufacturer = ?, modell = ? WHERE electronic_id = ?", Statement.RETURN_GENERATED_KEYS);

        if (articleGeneratedKeys.next()) {
            electronicStatement.setString(1,electronic.getProducer());
            electronicStatement.setString(2,electronic.getModel());
            electronicStatement.setInt(3,electronic.getId());
            electronicStatement.executeUpdate();

        }
        else {
            throw new SQLException("Updating electronic failed, no ID obtained.");
        }

        return electronic;
    }


    public Result getAllElectronics(){
        try {
            connection = db.getConnection();



            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN electronics on articles.article_id = electronics.electronic_id LEFT JOIN purchase on articles.article_id  = purchase.article_id WHERE purchase.purchase_id IS NULL;").executeQuery();
            ArrayList<Electronic> list = new ArrayList<>();

            while(resultSet.next()){
                Electronic electronic = new Electronic(resultSet.getString("name"),resultSet.getDouble("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"electronic",resultSet.getString("manufacturer"),resultSet.getString("modell"));
                electronic.setId(resultSet.getInt("article_id"));
                list.add(electronic);
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


    public Result getOneElectronic(Integer id){
        try {
            return ok(Json.toJson(getOneRawElectronic(id)));
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


    public Electronic getOneRawElectronic(Integer id) throws SQLException{

        connection = db.getConnection();

        ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN electronics on articles.article_id = electronics.electronic_id WHERE article_id ="+id+"").executeQuery();

        if(resultSet.next()){
            Electronic electronic = new Electronic(resultSet.getString("name"),resultSet.getDouble("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"electronic",resultSet.getString("manufacturer"),resultSet.getString("modell"));
            electronic.setId(resultSet.getInt("article_id"));

            connection.close();
            return electronic;
        }

        connection.close();
        throw new SQLException("No electronic with given ID found");/**/

    }

}
