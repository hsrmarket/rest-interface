package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import play.db.*;
import play.libs.Json;
import play.mvc.*;
import views.html.*;
import model.*;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;


public class BookController extends Controller {

    private Database db;

    @Inject
    public BookController(Database db) {
        this.db = db;
    }


    public Result getAll(){


        return null;
    }

    public Result insert(){
        JsonNode json = request().body().asJson();

        if(json == null){
            DefaultErrorMessage errorMessage = new DefaultErrorMessage();
            errorMessage.setId(0);
            errorMessage.setMessage("Expecting Json data");
            return badRequest(Json.toJson(errorMessage));
        }else{
            Integer id = json.findPath("id").intValue();
            String iban = json.findPath("iban").textValue();
            String author = json.findPath("author").textValue();
            if (id == null || iban == null || author == null) {
                DefaultErrorMessage errorMessage = new DefaultErrorMessage();
                errorMessage.setId(0);
                errorMessage.setMessage("Missing parameter(s)");
                return badRequest(Json.toJson(errorMessage));
            }else{
                Connection connection = db.getConnection();
                try {
                    connection.prepareStatement("insert into MOCK_DATA (ID, IBAN, Author) values ("+id+", '"+iban+"', '"+author+"')").execute();
                    DefaultSuccessMessage successMessage =  new DefaultSuccessMessage();
                    successMessage.setId(1);
                    successMessage.setMessage("Dataset successfully insert");
                    return ok(Json.toJson(successMessage));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return ok(index.render("Something went wrong"));
    }
}
