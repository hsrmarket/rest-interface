package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Controller;
import models.*;

public class ArticleController extends Controller {

    public Result insertArticle(){

        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }else{
            String type = json.findPath("type").toString();
            if(type == null){
                return badRequest(Json.toJson(new DefaultErrorMessage(12,"Missing Parameter (type)")));
            }

            switch (type) {

                case "book":
                    break;

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
