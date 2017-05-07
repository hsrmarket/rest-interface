package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import play.db.Database;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Controller;
import models.*;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ArticleController extends Controller {

    private Database db;
    private Connection connection;

    @Inject
    public ArticleController(Database db) {
        this.db = db;
    }


    public Result insertArticle(){

        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }else{
            String type = json.findPath("type").textValue();
            if(type == null || type.isEmpty()){
                return badRequest(Json.toJson(new DefaultErrorMessage(12,"Missing Parameter (type)")));
            }

            switch (type) {

                case "book":
                    BookController bc = new BookController(db);
                    return bc.insertBook();

                case "electronic":
                    ElectronicController ec = new ElectronicController(db);
                    return ec.insertElectronic();

                case "office supply":
                    OfficeSupplyController osc = new OfficeSupplyController(db);
                    return osc.insertOfficeSupply();

                case "other":
                    OtherArticleController oac = new OtherArticleController(db);
                    return oac.insertOtherArticle();

                default:
                    return badRequest(Json.toJson(new DefaultErrorMessage(13,"No matching type object")));

            }
        }
    }


    public Result getAllArticles(){

        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles LEFT JOIN purchase on articles.article_id  = purchase.article_id WHERE purchase.purchase_id IS NULL;").executeQuery();
            ArrayList<Article> list = new ArrayList<>();

            while(resultSet.next()){
                Article article = new Article(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"article");
                article.setId(resultSet.getInt("article_id"));
                list.add(article);
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


    public Result getOneArticle(Integer id){
        try {
            return ok(Json.toJson(getOneRawArticle(id)));
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


    public Article getOneRawArticle(Integer id) throws SQLException{

        String table = inWhichTable(id);
        switch (table){

            case "books":
                BookController bc = new BookController(db);
                return bc.getOneRawBook(id);

            case "electronics":
                ElectronicController ec = new ElectronicController(db);
                return ec.getOneRawElectronic(id);

            case "officeSupplies":
                OfficeSupplyController osc = new OfficeSupplyController(db);
                return osc.getOneRawOfficeSupply(id);

            case "otherarticles":
                OtherArticleController oac = new OtherArticleController(db);
                return oac.getOneRawOtherArticle(id);

            default:
                throw new SQLException("No article with given ID found");
        }


    }


    public Result updateOneArticle(Integer id){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }else if(id == null || json.findPath("type").textValue().isEmpty()){
            return badRequest(Json.toJson(new DefaultErrorMessage(12,"Missing Parameter (ID or Type)")));
        }

        switch (json.findPath("type").textValue()){

            case "book":
                BookController bc = new BookController(db);
                return bc.updateOneBook(id);

            case "electronic":
                ElectronicController ec = new ElectronicController(db);
                return ec.updateOneElectronic(id);

            case "office supply":
                OfficeSupplyController osc = new OfficeSupplyController(db);
                return osc.updateOneOfficeSupply(id);

            case "other":
                OtherArticleController oac = new OtherArticleController(db);
                return oac.updateOneOtherArticle(id);

            default:
                return badRequest(Json.toJson(new DefaultErrorMessage(13,"No matching type object")));
        }

    }


    public Result getRecentArticles(){

        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles ORDER BY creationdate DESC LIMIT 5").executeQuery();
            ArrayList<Article> list = new ArrayList<>();

            while(resultSet.next()){
                Article article = new Article(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"article");
                article.setId(resultSet.getInt("article_id"));
                list.add(article);
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


    public Result getAllArticlesFromSearch(){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }
        String search = json.findPath("search").textValue();

        if(search == null || search.isEmpty()){
            return badRequest(Json.toJson(new DefaultErrorMessage(12,"Missing Parameter (search)")));
        }

        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles WHERE 1=1 AND LOWER(name) LIKE LOWER('%"+search+"%') OR LOWER(description) LIKE LOWER('%"+search+"%') ORDER BY article_id").executeQuery();
            ArrayList<Article> list = new ArrayList<>();

            while(resultSet.next()){
                Article article = new Article(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"article");
                article.setId(resultSet.getInt("article_id"));
                list.add(article);
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


    private String inWhichTable(Integer id) throws SQLException{

        connection = db.getConnection();

        ResultSet bookResultSet = connection.prepareStatement("SELECT * FROM books where book_id ="+id+"").executeQuery();
        ResultSet electronicResultSet = connection.prepareStatement("SELECT * FROM electronics where electronic_id ="+id+"").executeQuery();
        ResultSet officeSupplyResultSet = connection.prepareStatement("SELECT * FROM officesupplies where officesupplie_id ="+id+"").executeQuery();
        ResultSet otherArticleResultSet = connection.prepareStatement("SELECT * FROM otherarticles where otherarticle_id ="+id+"").executeQuery();


        if(bookResultSet.next()){
        	connection.close();
            return "books";
        }else if(electronicResultSet.next()){
        	connection.close();
            return "electronics";
        }else if(officeSupplyResultSet.next()){
        	connection.close();
            return "officeSupplies";
        }else if(otherArticleResultSet.next()){
        	connection.close();
            return "otherarticles";
        }

		connection.close();
        return "none";
    }

}
