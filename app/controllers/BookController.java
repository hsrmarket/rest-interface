package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import play.db.*;
import play.libs.Json;
import play.mvc.*;
import views.html.*;
import models.*;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class BookController extends Controller {

    private Database db;

    @Inject
    public BookController(Database db) {
        this.db = db;
    }


    public Result getAll(){

        Connection connection = db.getConnection();
        try {
            ResultSet resultSet = connection.prepareStatement("select id, iban, author from MOCK_DATA").executeQuery();
            ArrayList<Book> list = new ArrayList<>();;

            while(resultSet.next()){
                Book book = new Book();
                book.setId(resultSet.getInt("id"));
                book.setIban(resultSet.getString("iban"));
                book.setAuthor(resultSet.getString("author"));
                list.add(book);
            }

            return ok(Json.toJson(list));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return badRequest(index.render("Something went wrong"));
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
                } finally {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return badRequest(index.render("Something went wrong"));
    }
}
