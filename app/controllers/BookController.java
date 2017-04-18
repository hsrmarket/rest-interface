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


    public Result insertBook(){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        Book book = new Book(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").textValue()),json.findPath("image").textValue(),json.findPath("isbn").textValue(),json.findPath("author").textValue(),json.findPath("publisher").textValue());
        //Book book = new Book("Test",15,5,"Test description",Date.valueOf("2017-04-15"),"test.png","934-23423-23","HSR","HSR");
        return insertBook(book);
    }


    public Result insertBook(Book book){

        try (
            Connection connection = db.getConnection();
            PreparedStatement articleStatement = connection.prepareStatement("INSERT INTO articles (name, description, condition, price, creationdate, image) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        ){
            articleStatement.setString(1,book.getName());
            articleStatement.setString(2,book.getDescription());
            articleStatement.setInt(3,book.getCondition());
            articleStatement.setInt(4,book.getPrice());
            articleStatement.setDate(5,book.getCreationDate());
            articleStatement.setString(6,book.getImage());

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


    public Result getAllBooks(){

        try (Connection connection = db.getConnection()){

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN books on articles.article_id = books.book_id").executeQuery();
            ArrayList<Book> list = new ArrayList<>();

            while(resultSet.next()){
                Book book = new Book(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),resultSet.getString("isbn"),resultSet.getString("author"),resultSet.getString("verlag"));
                book.setId(resultSet.getInt("article_id"));
                list.add(book);
            }

            return ok(Json.toJson(list));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }
    }


/*
    public Result getAll(){

        Connection connection = db.getConnection();
        try {
            ResultSet resultSet = connection.prepareStatement("select id, iban, author from MOCK_DATA").executeQuery();
            ArrayList<Book> list = new ArrayList<>();

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
