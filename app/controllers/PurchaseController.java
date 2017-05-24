package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.db.Database;
import play.libs.Json;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;

public class PurchaseController extends Controller {

    private Database db;
    private Connection connection;
    @Inject MailerClient mailerClient;

    @Inject
    public PurchaseController(Database db) {
        this.db = db;
    }


    public Result insertPurchase(){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        //set null because it's not relevant for purchases
        Article article = new Article(null,null,null,null,null,null,null);
        article.setId(json.get("article").findPath("id").asInt());
        Account account = new Account(null,null,null,null,null,null,null,false);
        account.setId(json.get("buyer").findPath("id").asInt());

        Account seller = new Account(null,null,null,null,null,null,null,false);

        connection = db.getConnection();
        ResultSet resultSet = null;

        int accountIDQuery = 0;
        String accountEmailQuery = null;

        try {
            resultSet = connection.prepareStatement("SELECT accounts.account_id, email FROM articleaccountallocation INNER JOIN accounts ON articleaccountallocation.account_id = accounts.account_id WHERE article_id = '" + article.getId() + "'").executeQuery();
            boolean isEmpty = resultSet.next();
            accountIDQuery = resultSet.getInt("account_id");
            accountEmailQuery = resultSet.getString("email");
            resultSet.close();
            connection.close();
        } catch (SQLException e) {

        }
        seller.setId(accountIDQuery);
        seller.setEmail(accountEmailQuery);
        Purchase purchase = new Purchase(article,account,json.findPath("completed").asBoolean(),Date.valueOf(json.findPath("purchaseDate").asText()),seller);
        //Properties checker

        try {
            //sendEmail(purchase);
            return ok(Json.toJson(insertPurchase(purchase)));
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


    private Purchase insertPurchase(Purchase purchase) throws SQLException{

        connection = db.getConnection();
        PreparedStatement purchaseStatement = connection.prepareStatement("INSERT INTO purchase (article_id, buyer_id, iscompleted, purchasedate, seller_id) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

        purchaseStatement.setInt(1,purchase.getArticle().getId());
        purchaseStatement.setInt(2,purchase.getBuyer().getId());
        purchaseStatement.setBoolean(3,purchase.getCompleted());
        purchaseStatement.setDate(4,purchase.getPurchaseDate());
        purchaseStatement.setInt(5,purchase.getSeller().getId());

        int affectedRows = purchaseStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating purchase failed, no rows affected.");
        }

        ResultSet purchaseGeneratedKeys = purchaseStatement.getGeneratedKeys();
        purchaseGeneratedKeys.next();
        purchase.setId(purchaseGeneratedKeys.getInt(1));

        connection.close();

        return purchase;
    }


    public Result updatePurchase(Integer id){

        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        Boolean isCompleted = json.findPath("completed").asBoolean();

        try {
            connection = db.getConnection();
            PreparedStatement purchaseStatement = connection.prepareStatement("UPDATE purchase SET iscompleted = ? WHERE purchase_id = ?", Statement.RETURN_GENERATED_KEYS);

            purchaseStatement.setBoolean(1, isCompleted);
            purchaseStatement.setInt(2, id);

            int affectedRows = purchaseStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating purchase failed, no rows affected.");
            }

            return ok(Json.toJson(new DefaultSuccessMessage(0,"purchase successfully updated")));

        }catch (SQLException e){
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
        }
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
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM purchase INNER JOIN articles on purchase.article_id = articles.article_id INNER JOIN accounts as buyer on purchase.buyer_id  = buyer.account_id INNER JOIN accounts as seller on purchase.seller_id = seller.account_id").executeQuery();
            ArrayList<Purchase> list = new ArrayList<>();
            AccountController accountController = new AccountController(db);
            ArticleController articleController = new ArticleController(db);

            while(resultSet.next()){
                Purchase purchase = new Purchase(articleController.getOneRawArticle(resultSet.getInt("article_id")),accountController.getOneRawAccount(resultSet.getInt("buyer_id")),resultSet.getBoolean("iscompleted"),resultSet.getDate("purchasedate"),accountController.getOneRawAccount(resultSet.getInt("seller_id")));
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
            AccountController accountController = new AccountController(db);
            ArticleController articleController = new ArticleController(db);

            if(resultSet.next()){
                Purchase purchase = new Purchase(articleController.getOneRawArticle(resultSet.getInt("article_id")),accountController.getOneRawAccount(resultSet.getInt("buyer_id")),resultSet.getBoolean("iscompleted"),resultSet.getDate("purchasedate"),accountController.getOneRawAccount(resultSet.getInt("seller_id")));
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


    public void sendEmail(Purchase purchase) {
        Email email = new Email()
                .setSubject("New purchase on HSRmarket")
                .setFrom("info@hsrmarket.ch")
                .addTo(purchase.getSeller().getEmail())
                .setBodyText("Dear customer, You have sold one of your articles on HSRmarket. Best wishes your HSRmarket-Team");

            mailerClient.send(email);
    }


}
