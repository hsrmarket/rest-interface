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
            if(type.isEmpty()){
                return badRequest(Json.toJson(new DefaultErrorMessage(12,"Missing Parameter (type)")));
            }

            switch (type) {

                case "book":
                    Book book = new Book(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),json.findPath("isbn").textValue(),json.findPath("author").textValue(),json.findPath("publisher").textValue());
                    //Properties checker
                    BookController bc = new BookController(db);
                    return bc.insertBook(book);

                case "electronic":
                    Electronic electronic = new Electronic(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),json.findPath("producer").textValue(),json.findPath("model").textValue());
                    //Properties checker
                    ElectronicController ec = new ElectronicController(db);
                    return ec.insertElectronic(electronic);

                case "office supply":
                    OfficeSupply officeSupply = new OfficeSupply(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue());
                    //Properties checker
                    OfficeSupplyController osc = new OfficeSupplyController(db);
                    return osc.insertOfficeSupply(officeSupply);

                case "other":
                    OtherArticle otherArticle = new OtherArticle(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue());
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
                Article article = new Article(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"));
                article.setId(resultSet.getInt("article_id"));
                list.add(article);
            }

            return ok(Json.toJson(list));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }
    }


    public Result getOneArticle(Integer id){

        try (Connection connection = db.getConnection()){

            ResultSet bookResultSet = connection.prepareStatement("SELECT * FROM books where book_id ="+id+"").executeQuery();
            ResultSet electronicResultSet = connection.prepareStatement("SELECT * FROM electronics where electronic_id ="+id+"").executeQuery();
            ResultSet officeSupplyResultSet = connection.prepareStatement("SELECT * FROM officesupplies where officesupplie_id ="+id+"").executeQuery();
            ResultSet otherArticleResultSet = connection.prepareStatement("SELECT * FROM otherarticles where otherarticle_id ="+id+"").executeQuery();

            if(bookResultSet.next()){

                BookController bc = new BookController(db);
                return bc.getOneBook(id);

            }else if(electronicResultSet.next()){

                ElectronicController ec = new ElectronicController(db);
                return ec.getOneElectronic(id);

            }else if(officeSupplyResultSet.next()){

                OfficeSupplyController osc = new OfficeSupplyController(db);
                return osc.getOneOfficeSupply(id);

            }else if(otherArticleResultSet.next()){

                OtherArticleController oac = new OtherArticleController(db);
                return oac.getOneOtherArticle(id);

            }

            return badRequest(Json.toJson(new DefaultErrorMessage(14,"No article with given ID found")));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }

    }


    public Result getRecentArticles(){

        try (Connection connection = db.getConnection()){

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles ORDER BY creationdate DESC LIMIT 5").executeQuery();
            ArrayList<Article> list = new ArrayList<>();

            while(resultSet.next()){
                Article article = new Article(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"));
                article.setId(resultSet.getInt("article_id"));
                list.add(article);
            }

            return ok(Json.toJson(list));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }
    }
    
}
