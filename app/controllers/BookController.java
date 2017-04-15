package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import play.db.*;
import play.libs.Json;
import play.mvc.*;
import models.*;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;


public class BookController extends Controller {

    private Database db;

    @Inject
    public BookController(Database db) {
        this.db = db;
    }


    public Result insertBook(Book book){

        try (
            Connection connection = db.getConnection();
            PreparedStatement articleStatement = connection.prepareStatement("INSERT INTO articles (name, description, condition, price, creationdate) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        ){
            articleStatement.setString(1,book.getName());
            articleStatement.setString(2,book.getDescription());
            articleStatement.setInt(3,book.getCondition());
            articleStatement.setInt(4,book.getPrice());
            articleStatement.setDate(5,book.getCreationDate());

            int affectedRows = articleStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating book failed, no rows affected.");
            }

            try (
                ResultSet articleGeneratedKeys = articleStatement.getGeneratedKeys();
                PreparedStatement bookStatement = connection.prepareStatement("INSERT INTO books (book_id, author, verlag, isbn) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ) {
                if (articleGeneratedKeys.next()) {
                    bookStatement.setInt(1,articleGeneratedKeys.getInt(1));
                    bookStatement.setString(2,book.getAuthor());
                    bookStatement.setString(3,book.getPublisher());
                    bookStatement.setString(4,book.getIsbn());
                    bookStatement.executeUpdate();

                    book.setId(articleGeneratedKeys.getInt(1));

                }
                else {
                    throw new SQLException("Creating book failed, no ID obtained.");
                }
            }
        }catch (SQLException e){
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        }
        return ok(Json.toJson(book));
    }


/*
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
    */
}
