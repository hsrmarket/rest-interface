package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import play.db.Database;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Controller;
import models.*;

import javax.inject.Inject;
import java.sql.Date;

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
                    Book book = new Book(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").textValue()),json.findPath("isbn").textValue(),json.findPath("author").textValue(),json.findPath("publisher").textValue(),json.findPath("image").textValue());
                    BookController bc = new BookController(db);
                    return bc.insertBook(book);

                case "electronic":
                    break;

                case "office supply":
                    break;

                case "other":
                    break;

            }
        }

        return badRequest(Json.toJson(new DefaultErrorMessage(1,"Something went wrong")));
    }

}
