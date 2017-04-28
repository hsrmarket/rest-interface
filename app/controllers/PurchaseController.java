package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import models.DefaultErrorMessage;
import models.DefaultSuccessMessage;
import models.Purchase;
import play.db.Database;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;

public class PurchaseController extends Controller {

    private Database db;
    private Connection connection;

    @Inject
    public PurchaseController(Database db) {
        this.db = db;
    }


    public Result insertPurchase(){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        Purchase purchase = new Purchase(json.findPath("articleId").asInt(),json.findPath("buyerId").asInt(),json.findPath("completed").asBoolean(),Date.valueOf(json.findPath("purchaseDate").asText()));
        //Properties checker
        return insertPurchase(purchase);
    }


    private Result insertPurchase(Purchase purchase){
        try {

            connection = db.getConnection();
            PreparedStatement purchaseStatement = connection.prepareStatement("INSERT INTO purchase (article_id, buyer_id, iscompleted, purchasedate) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

            purchaseStatement.setInt(1,purchase.getArticleId());
            purchaseStatement.setInt(2,purchase.getBuyerId());
            purchaseStatement.setBoolean(3,purchase.getCompleted());
            purchaseStatement.setDate(4,purchase.getPurchaseDate());

            int affectedRows = purchaseStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating address failed, no rows affected.");
            }

            ResultSet purchaseGeneratedKeys = purchaseStatement.getGeneratedKeys();
            purchaseGeneratedKeys.next();
            purchase.setId(purchaseGeneratedKeys.getInt(1));

        }catch (SQLException e){
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
        }
        return ok(Json.toJson(purchase));
    }


    public Result deletePurchase(Integer id){
        try {
            connection = db.getConnection();

            PreparedStatement deletePurchaseStatement = connection.prepareStatement("DELETE FROM purchase WHERE purchase_id = "+id+"", Statement.RETURN_GENERATED_KEYS);
            int affectedRow = deletePurchaseStatement.executeUpdate();

            if(affectedRow != 0) {
                return ok(Json.toJson(new DefaultSuccessMessage(0, "Purchase successfully deleted")));
            } else {
                throw new SQLException("Deleting purchase failed, no rows affected.");
            }

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


    public Result getAllPurchases(){
        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM purchase").executeQuery();
            ArrayList<Purchase> list = new ArrayList<>();
            AccountController ac = new AccountController(db);

            while(resultSet.next()){
                Purchase purchase = new Purchase(resultSet.getInt("article_id"),ac.getOneRawAccount(resultSet.getInt("buyer_id")),resultSet.getBoolean("iscompleted"),resultSet.getDate("purchasedate"));
                purchase.setId(resultSet.getInt("purchase_id"));
                list.add(purchase);
            }

            return ok(Json.toJson(list));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
        }
    }


    public Result getOnePurchase(Integer id){
        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM purchase WHERE purchase_id ='"+id+"'").executeQuery();

            if(resultSet.next()){
                Purchase purchase = new Purchase(resultSet.getInt("article_id"),resultSet.getInt("buyer_id"),resultSet.getBoolean("iscompleted"),resultSet.getDate("purchasedate"));
                purchase.setId(resultSet.getInt("purchase_id"));
                return ok(Json.toJson(purchase));
            }

            return badRequest(Json.toJson(new DefaultErrorMessage(14,"No purchase with given ID found")));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
        }
    }

}
