package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.DefaultErrorMessage;
import models.OfficeSupply;
import play.db.Database;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;


public class OfficeSupplyController extends Controller {

    private Database db;
    private Connection connection;

    @Inject
    public OfficeSupplyController(Database db) {
        this.db = db;
    }


    public Result insertOfficeSupply(){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        OfficeSupply officeSupply = new OfficeSupply(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"office supply");
        //Properties checker
        try {
            return ok(Json.toJson(insertOfficeSupply(officeSupply)));
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


    public OfficeSupply insertOfficeSupply(OfficeSupply officeSupply) throws SQLException{

        connection = db.getConnection();
        PreparedStatement articleStatement = connection.prepareStatement("INSERT INTO articles (name, description, condition, price, creationdate, image) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

        articleStatement.setString(1,officeSupply.getName());
        articleStatement.setString(2,officeSupply.getDescription());
        articleStatement.setInt(3,officeSupply.getCondition());
        articleStatement.setInt(4,officeSupply.getPrice());
        articleStatement.setDate(5,officeSupply.getCreationDate());
        articleStatement.setString(6,officeSupply.getImage());

        int affectedRows = articleStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating office supply failed, no rows affected.");
        }

        ResultSet articleGeneratedKeys = articleStatement.getGeneratedKeys();
        PreparedStatement officeSupplyStatement = connection.prepareStatement("INSERT INTO officesupplies (officesupplie_id) VALUES (?)", Statement.RETURN_GENERATED_KEYS);

        if (articleGeneratedKeys.next()) {
            officeSupplyStatement.setInt(1,articleGeneratedKeys.getInt(1));
            officeSupplyStatement.executeUpdate();

            officeSupply.setId(articleGeneratedKeys.getInt(1));

        } else {
            throw new SQLException("Creating office supply failed, no ID obtained.");
        }

        return officeSupply;
    }


    public Result updateOneOfficeSupply(Integer id){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        if(id == null){
            return badRequest(Json.toJson(new DefaultErrorMessage(12,"Missing Parameter (ID)")));
        }

        OfficeSupply officeSupply = new OfficeSupply(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"office supply");
        //Properties checker
        officeSupply.setId(json.findPath("id").intValue());

        try {
            return ok(Json.toJson(updateOneOfficeSupply(officeSupply)));
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


    public OfficeSupply updateOneOfficeSupply(OfficeSupply officeSupply) throws SQLException{

        connection = db.getConnection();
        PreparedStatement articleStatement = connection.prepareStatement("UPDATE articles SET name = ?, description = ?, condition = ?, price = ?, creationdate = ?, image = ? WHERE article_id = ?", Statement.RETURN_GENERATED_KEYS);

        articleStatement.setString(1,officeSupply.getName());
        articleStatement.setString(2,officeSupply.getDescription());
        articleStatement.setInt(3,officeSupply.getCondition());
        articleStatement.setInt(4,officeSupply.getPrice());
        articleStatement.setDate(5,officeSupply.getCreationDate());
        articleStatement.setString(6,officeSupply.getImage());
        articleStatement.setInt(7,officeSupply.getId());

        int affectedRows = articleStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Updating office supply failed, no rows affected.");
        }

        return officeSupply;
    }


    public Result getAllOfficeSupplies(){

        try {
            connection = db.getConnection();

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN officesupplies on articles.article_id = officesupplies.officesupplie_id;").executeQuery();
            ArrayList<OfficeSupply> list = new ArrayList<>();

            while(resultSet.next()){
                OfficeSupply officeSupply = new OfficeSupply(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"office supply");
                officeSupply.setId(resultSet.getInt("article_id"));
                list.add(officeSupply);
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


    public Result getOneOfficeSupply(Integer id){

        try {
            connection = db.getConnection();

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN officesupplies on articles.article_id = officesupplies.officesupplie_id WHERE article_id ="+id+"").executeQuery();

            if(resultSet.next()){
                OfficeSupply officeSupply = new OfficeSupply(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"office supply");
                officeSupply.setId(resultSet.getInt("article_id"));
                return ok(Json.toJson(officeSupply));
            }

            return badRequest(Json.toJson(new DefaultErrorMessage(14,"No office supply with given ID found")));

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
}
