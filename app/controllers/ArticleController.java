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
                    Book book = new Book(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"book",json.findPath("isbn").textValue(),json.findPath("author").textValue(),json.findPath("publisher").textValue());
                    //Properties checker
                    BookController bc = new BookController(db);
                    return bc.insertBook(book);

                case "electronic":
                    Electronic electronic = new Electronic(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"electronic",json.findPath("producer").textValue(),json.findPath("model").textValue());
                    //Properties checker
                    ElectronicController ec = new ElectronicController(db);
                    return ec.insertElectronic(electronic);

                case "office supply":
                    OfficeSupply officeSupply = new OfficeSupply(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"office supply");
                    //Properties checker
                    OfficeSupplyController osc = new OfficeSupplyController(db);
                    return osc.insertOfficeSupply(officeSupply);

                case "other":
                    OtherArticle otherArticle = new OtherArticle(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"other");
                    //Properties checker
                    OtherArticleController oac = new OtherArticleController(db);
                    return oac.insertOtherArticle(otherArticle);

                default:
                    return badRequest(Json.toJson(new DefaultErrorMessage(13,"No matching type object")));

            }
        }
    }


    public Result getAllArticles(){

        try (Connection connection = db.getConnection()){

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles").executeQuery();
            ArrayList<Article> list = new ArrayList<>();

            while(resultSet.next()){
                Article article = new Article(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"article");
                article.setId(resultSet.getInt("article_id"));
                list.add(article);
            }

            return ok(Json.toJson(list));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }
    }


    public Result getOneArticle(Integer id){

        try {
            String table = inWhichTable(id);
            switch (table){

                case "books":
                    BookController bc = new BookController(db);
                    return bc.getOneBook(id);

                case "electronics":
                    ElectronicController ec = new ElectronicController(db);
                    return ec.getOneElectronic(id);

                case "officeSupplies":
                    OfficeSupplyController osc = new OfficeSupplyController(db);
                    return osc.getOneOfficeSupply(id);

                case "otherarticles":
                    OtherArticleController oac = new OtherArticleController(db);
                    return oac.getOneOtherArticle(id);

                default:
                    return badRequest(Json.toJson(new DefaultErrorMessage(14,"No article with given ID found")));
            }

        }catch (SQLException e){
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
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
                Book book = new Book(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"book",json.findPath("isbn").textValue(),json.findPath("author").textValue(),json.findPath("publisher").textValue());
                book.setId(json.findPath("id").intValue());
                //Properties checker
                BookController bc = new BookController(db);
                return bc.updateOneBook(book);

            case "electronic":
                Electronic electronic = new Electronic(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"electronic",json.findPath("producer").textValue(),json.findPath("model").textValue());
                electronic.setId(json.findPath("id").intValue());
                //Properties checker
                ElectronicController ec = new ElectronicController(db);
                return ec.updateOneElectronic(electronic);

            case "office supply":
                OfficeSupply officeSupply = new OfficeSupply(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"office supply");
                officeSupply.setId(json.findPath("id").intValue());
                //Properties checker
                OfficeSupplyController osc = new OfficeSupplyController(db);
                return osc.updateOneOfficeSupply(officeSupply);

            case "other":
                OtherArticle otherArticle = new OtherArticle(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"other");
                otherArticle.setId(json.findPath("id").intValue());
                //Properties checker
                OtherArticleController oac = new OtherArticleController(db);
                return oac.updateOneOtherArticle(otherArticle);

            default:
                return badRequest(Json.toJson(new DefaultErrorMessage(13,"No matching type object")));
        }

    }


    public Result getRecentArticles(){

        try (Connection connection = db.getConnection()){

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

        }
    }


    private String inWhichTable(Integer id) throws SQLException{

        Connection connection = db.getConnection();

        ResultSet bookResultSet = connection.prepareStatement("SELECT * FROM books where book_id ="+id+"").executeQuery();
        ResultSet electronicResultSet = connection.prepareStatement("SELECT * FROM electronics where electronic_id ="+id+"").executeQuery();
        ResultSet officeSupplyResultSet = connection.prepareStatement("SELECT * FROM officesupplies where officesupplie_id ="+id+"").executeQuery();
        ResultSet otherArticleResultSet = connection.prepareStatement("SELECT * FROM otherarticles where otherarticle_id ="+id+"").executeQuery();


        if(bookResultSet.next()){
            return "books";
        }else if(electronicResultSet.next()){
            return "electronics";
        }else if(officeSupplyResultSet.next()){
            return "officeSupplies";
        }else if(otherArticleResultSet.next()){
            return "otherarticles";
        }

        return "none";
    }

}
