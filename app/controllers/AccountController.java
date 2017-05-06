package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.db.Database;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;


public class AccountController extends Controller {

    private Database db;
    private Connection connection;

    @Inject
    public AccountController(Database db) {
        this.db = db;
    }


    public Result insertAccount(){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        Address address = new Address(json.findPath("street").textValue(),json.findPath("streetNr").textValue(),json.findPath("zip").asInt(),json.findPath("city").textValue());
        Account account = new Account(json.findPath("studentId").asInt(),json.findPath("firstname").textValue(),json.findPath("lastname").textValue(),address,json.findPath("email").textValue(),json.findPath("telephone").textValue(),json.findPath("password").textValue(),json.findPath("admin").asBoolean());
        //Properties checker
        try {
            return ok(Json.toJson(insertAccount(account)));
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


    private Account insertAccount(Account account) throws SQLException {

        connection = db.getConnection();
        PreparedStatement addressStatement = connection.prepareStatement("INSERT INTO address (streetname, streetnumber, zip, city) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

        addressStatement.setString(1,account.getAddress().getStreet());
        addressStatement.setString(2,account.getAddress().getStreetNr());
        addressStatement.setInt(3,account.getAddress().getZip());
        addressStatement.setString(4,account.getAddress().getCity());

        int affectedRows = addressStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating address failed, no rows affected.");
        }

        ResultSet addressGeneratedKeys = addressStatement.getGeneratedKeys();
        PreparedStatement accountStatement = connection.prepareStatement("INSERT INTO accounts (address_id, firstname, lastname, studentid, email, tel, pw, isadmin) VALUES (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

        if (addressGeneratedKeys.next()) {
            accountStatement.setInt(1,addressGeneratedKeys.getInt(1));
            accountStatement.setString(2,account.getFirstname());
            accountStatement.setString(3,account.getLastname());
            accountStatement.setInt(4,account.getStudentId());
            accountStatement.setString(5,account.getEmail());
            accountStatement.setString(6,account.getTelephone());
            accountStatement.setString(7,account.getPassword());
            accountStatement.setBoolean(8,account.isAdmin());

            accountStatement.executeUpdate();

            ResultSet accountGeneratedKeys = accountStatement.getGeneratedKeys();

            accountGeneratedKeys.next();
            account.setId(accountGeneratedKeys.getInt(1));
            account.getAddress().setId(addressGeneratedKeys.getInt(1));

        } else {
            throw new SQLException("Creating account failed, no ID obtained.");
        }

        return account;
    }


    public Result updateOneAccount(Integer id){
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        if(id == null){
            return badRequest(Json.toJson(new DefaultErrorMessage(12,"Missing Parameter (ID)")));
        }

        Address address = new Address(json.findPath("street").textValue(),json.findPath("streetNr").textValue(),json.findPath("zip").asInt(),json.findPath("city").textValue());
        Account account = new Account(json.findPath("studentId").asInt(),json.findPath("firstname").textValue(),json.findPath("lastname").textValue(),address,json.findPath("email").textValue(),json.findPath("telephone").textValue(),json.findPath("password").textValue(),json.findPath("admin").asBoolean());

        //Properties checker
        address.setId(json.get("address").findPath("id").asInt());
        account.setId(json.findPath("id").asInt());

        try {
            return ok(Json.toJson(updateOneAccount(account)));
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


    private Account updateOneAccount(Account account) throws SQLException{

        connection = db.getConnection();
        PreparedStatement addressStatement = connection.prepareStatement("UPDATE address SET streetname = ?, streetnumber = ?, zip = ?, city = ? WHERE address_id = ?", Statement.RETURN_GENERATED_KEYS);

        addressStatement.setString(1,account.getAddress().getStreet());
        addressStatement.setString(2,account.getAddress().getStreetNr());
        addressStatement.setInt(3,account.getAddress().getZip());
        addressStatement.setString(4,account.getAddress().getCity());
        addressStatement.setInt(5,account.getAddress().getId());

        int affectedRows = addressStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Updating address failed, no rows affected.");
        }

        ResultSet addressGeneratedKeys = addressStatement.getGeneratedKeys();
        PreparedStatement accountStatement = connection.prepareStatement("UPDATE accounts SET firstname = ?, lastname = ?, studentid = ?, email = ?, tel = ?, pw = ?, isadmin = ? WHERE account_id = ?", Statement.RETURN_GENERATED_KEYS);

        if (addressGeneratedKeys.next()) {
            accountStatement.setString(1,account.getFirstname());
            accountStatement.setString(2,account.getLastname());
            accountStatement.setInt(3,account.getStudentId());
            accountStatement.setString(4,account.getEmail());
            accountStatement.setString(5,account.getTelephone());
            accountStatement.setString(6,account.getPassword());
            accountStatement.setBoolean(7,account.isAdmin());
            accountStatement.setInt(8,account.getId());

            accountStatement.executeUpdate();

        } else {
            throw new SQLException("Updating account failed, no ID obtained.");
        }

        return account;
    }


    public Result deleteAccount(Integer id){
        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT address_id FROM accounts WHERE account_id ="+id+"").executeQuery();

            if(resultSet.next()){
                Integer addressID = resultSet.getInt("address_id");

                PreparedStatement deleteAccountStatement = connection.prepareStatement("DELETE FROM accounts WHERE account_id = "+id+"", Statement.RETURN_GENERATED_KEYS);
                PreparedStatement deleteAddressStatement = connection.prepareStatement("DELETE FROM address WHERE address_id = "+addressID+"", Statement.RETURN_GENERATED_KEYS);
                deleteAccountStatement.executeUpdate();
                deleteAddressStatement.executeUpdate();

                return ok(Json.toJson(new DefaultSuccessMessage(0, "Account successfully deleted")));
            }

            return badRequest(Json.toJson(new DefaultErrorMessage(14,"No Account with given ID found")));

        }catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
        }
    }


    public Result getAllAccounts(){

        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM accounts INNER JOIN address ON accounts.address_id = address.address_id").executeQuery();
            ArrayList<Account> list = new ArrayList<>();

            while(resultSet.next()){
                Address address = new Address(resultSet.getString("streetname"),resultSet.getString("streetnumber"),resultSet.getInt("zip"),resultSet.getString("city"));
                address.setId(resultSet.getInt("address_id"));
                Account account = new Account(resultSet.getInt("studentid"),resultSet.getString("firstname"),resultSet.getString("lastname"),address,resultSet.getString("email"),resultSet.getString("tel"),resultSet.getString("pw"),resultSet.getBoolean("isadmin"));
                account.setId(resultSet.getInt("account_id"));
                list.add(account);
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


    public Result getOneAccount(Integer id){
        try {
            return ok(Json.toJson(getOneRawAccount(id)));
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


    public Account getOneRawAccount(Integer id) throws SQLException{

        connection = db.getConnection();
        ResultSet resultSet = connection.prepareStatement("SELECT * FROM accounts INNER JOIN address ON accounts.address_id = address.address_id WHERE account_id ='"+id+"'").executeQuery();

        if(resultSet.next()){
            Address address = new Address(resultSet.getString("streetname"),resultSet.getString("streetnumber"),resultSet.getInt("zip"),resultSet.getString("city"));
            address.setId(resultSet.getInt("address_id"));
            Account account = new Account(resultSet.getInt("studentid"),resultSet.getString("firstname"),resultSet.getString("lastname"),address,resultSet.getString("email"),resultSet.getString("tel"),resultSet.getString("pw"),resultSet.getBoolean("isadmin"));
            account.setId(resultSet.getInt("account_id"));
            try {
                connection.close();
            } catch (SQLException e) {
                throw new SQLException("No account with given ID found");
            }
            return account;
        }

        try {
            connection.close();
        } catch (SQLException e) {
            throw new SQLException("No account with given ID found");
        }
        throw new SQLException("No account with given ID found");
    }


    public Result getAdminAccounts(){

        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM accounts INNER JOIN address ON accounts.address_id = address.address_id WHERE isadmin='true'").executeQuery();
            ArrayList<Account> list = new ArrayList<>();

            while(resultSet.next()){
                Address address = new Address(resultSet.getString("streetname"),resultSet.getString("streetnumber"),resultSet.getInt("zip"),resultSet.getString("city"));
                address.setId(resultSet.getInt("address_id"));
                Account account = new Account(resultSet.getInt("studentid"),resultSet.getString("firstname"),resultSet.getString("lastname"),address,resultSet.getString("email"),resultSet.getString("tel"),resultSet.getString("pw"),resultSet.getBoolean("isadmin"));
                account.setId(resultSet.getInt("account_id"));
                list.add(account);
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


    public Result getNormalAccounts(){

        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM accounts INNER JOIN address ON accounts.address_id = address.address_id WHERE isadmin='false'").executeQuery();
            ArrayList<Account> list = new ArrayList<>();

            while(resultSet.next()){
                Address address = new Address(resultSet.getString("streetname"),resultSet.getString("streetnumber"),resultSet.getInt("zip"),resultSet.getString("city"));
                address.setId(resultSet.getInt("address_id"));
                Account account = new Account(resultSet.getInt("studentid"),resultSet.getString("firstname"),resultSet.getString("lastname"),address,resultSet.getString("email"),resultSet.getString("tel"),resultSet.getString("pw"),resultSet.getBoolean("isadmin"));
                account.setId(resultSet.getInt("account_id"));
                list.add(account);
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


    public Result login() {
        JsonNode json = request().body().asJson();

        if(json == null) {
            return badRequest(Json.toJson(new DefaultErrorMessage(11,"Expecting Json data")));
        }

        String mail = json.findPath("email").asText();
        String password = json.findPath("password").asText();

        connection = db.getConnection();
        ResultSet resultSet = null;
        try {
            resultSet = connection.prepareStatement("SELECT * FROM accounts WHERE email LIKE '" + mail + "' AND pw LIKE '" + password + "'").executeQuery();
            boolean isEmpty = resultSet.next();

            // Diese if Else mit doppelten Ausdrücken ist nötig, damit diese Methode so funktioniert wie sie sollte.
            // Aus Zeitgründen wurde auf eine weitere Ausarbeitung verzichtet.
            // Normalerweise würde mit finally die connection geschlossen, doch dies funktioniert hier aus
            // unerklärlichen Gründen nicht
            if (isEmpty == false) {
                resultSet.close();
                connection.close();
                return badRequest(Json.toJson(new DefaultErrorMessage(14, "Email or password is incorrect")));
            }
            else {
                int accountIDQuery = resultSet.getInt("account_id");
                resultSet.close();
                connection.close();
                Account account  = getOneRawAccount(accountIDQuery);
                return ok(Json.toJson(account));
            }

        } catch (SQLException e) {
            try {
                connection.close();
                resultSet.close();
            } catch (SQLException e1) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        }
    }


    public Result getAllArticlesFromAccount(Integer id){

        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articleaccountallocation WHERE account_id='"+id+"'").executeQuery();
            ArrayList<Article> list = new ArrayList<>();
            ArticleController articleController = new ArticleController(db);

            while(resultSet.next()){
                Article article = articleController.getOneRawArticle(resultSet.getInt("article_id"));
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


    public Result getAllBoughtArticlesFromAccount(Integer id){
        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM purchase WHERE buyer_id='"+id+"'").executeQuery();
            ArrayList<Article> list = new ArrayList<>();
            ArticleController articleController = new ArticleController(db);

            while(resultSet.next()){
                Article article = articleController.getOneRawArticle(resultSet.getInt("article_id"));
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

    public Result getAllSales(Integer id){
        try {
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM purchase WHERE seller_id='"+id+"'").executeQuery();
            ArrayList<Article> salesList = new ArrayList<>();
            ArticleController articleController = new ArticleController(db);

            while(resultSet.next()){
                Article article = articleController.getOneRawArticle(resultSet.getInt("article_id"));
                salesList.add(article);
            }

            return ok(Json.toJson(salesList));

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

}
