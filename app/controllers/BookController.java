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
    private Connection connection;

    @Inject
    public BookController(Database db) {
        this.db = db;
    }


    public Result insertBook(){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        Book book = new Book(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"book",json.findPath("isbn").textValue(),json.findPath("author").textValue(),json.findPath("publisher").textValue());
        //Properties checker

        try {
            return ok(Json.toJson(insertBook(book)));
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


    private Book insertBook(Book book) throws SQLException{

        connection = db.getConnection();
        PreparedStatement articleStatement = connection.prepareStatement("INSERT INTO articles (name, description, condition, price, creationdate, image) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

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


        ResultSet articleGeneratedKeys = articleStatement.getGeneratedKeys();
        PreparedStatement bookStatement = connection.prepareStatement("INSERT INTO books (book_id, author, publisher, isbn) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

        if (articleGeneratedKeys.next()) {
            bookStatement.setInt(1,articleGeneratedKeys.getInt(1));
            bookStatement.setString(2,book.getAuthor());
            bookStatement.setString(3,book.getPublisher());
            bookStatement.setString(4,book.getIsbn());
            bookStatement.executeUpdate();

            book.setId(articleGeneratedKeys.getInt(1));

        } else {
            throw new SQLException("Creating book failed, no ID obtained.");
        }

        return book;
    }


    public Result updateOneBook(Integer id){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        if(id == null){
            return badRequest(Json.toJson(new DefaultErrorMessage(12,"Missing Parameter (ID)")));
        }

        Book book = new Book(json.findPath("name").textValue(),json.findPath("price").intValue(),json.findPath("condition").intValue(),json.findPath("description").textValue(), Date.valueOf(json.findPath("creationDate").asText()),json.findPath("image").textValue(),"book",json.findPath("isbn").textValue(),json.findPath("author").textValue(),json.findPath("publisher").textValue());
        //Properties checker
        book.setId(json.findPath("id").intValue());

        try {
            return ok(Json.toJson(updateOneBook(book)));
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


    private Book updateOneBook(Book book) throws SQLException{

        connection = db.getConnection();
        PreparedStatement articleStatement = connection.prepareStatement("UPDATE articles SET name = ?, description = ?, condition = ?, price = ?, creationdate = ?, image = ? WHERE article_id = ?", Statement.RETURN_GENERATED_KEYS);

        articleStatement.setString(1,book.getName());
        articleStatement.setString(2,book.getDescription());
        articleStatement.setInt(3,book.getCondition());
        articleStatement.setInt(4,book.getPrice());
        articleStatement.setDate(5,book.getCreationDate());
        articleStatement.setString(6,book.getImage());
        articleStatement.setInt(7,book.getId());

        int affectedRows = articleStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Updating book failed, no rows affected.");
        }


        ResultSet articleGeneratedKeys = articleStatement.getGeneratedKeys();
        PreparedStatement bookStatement = connection.prepareStatement("UPDATE books SET author = ?, publisher = ?, isbn = ? WHERE book_id = ?", Statement.RETURN_GENERATED_KEYS);

        if (articleGeneratedKeys.next()) {
            bookStatement.setString(1,book.getAuthor());
            bookStatement.setString(2,book.getPublisher());
            bookStatement.setString(3,book.getIsbn());
            bookStatement.setInt(4,book.getId());
            bookStatement.executeUpdate();

        } else {
            throw new SQLException("Updating book failed, no ID obtained.");
        }

        return book;
    }


    public Result getAllBooks(){

        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN books on articles.article_id = books.book_id").executeQuery();
            ArrayList<Book> list = new ArrayList<>();

            while(resultSet.next()){
                Book book = new Book(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"book",resultSet.getString("isbn"),resultSet.getString("author"),resultSet.getString("publisher"));
                book.setId(resultSet.getInt("article_id"));
                list.add(book);
            }

            return ok(Json.toJson(list));

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


    public Result getOneBook(Integer id){
        try {
            return ok(Json.toJson(getOneRawBook(id)));
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


    public Book getOneRawBook(Integer id) throws SQLException{

        connection = db.getConnection();
        ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN books on articles.article_id = books.book_id WHERE article_id ="+id+"").executeQuery();

        if(resultSet.next()){
            Book book = new Book(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),"book",resultSet.getString("isbn"),resultSet.getString("author"),resultSet.getString("publisher"));
            book.setId(resultSet.getInt("article_id"));

            connection.close();
            return book;
        }

        connection.close();
        throw new SQLException("No book with given ID found");

    }

}
