package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.Address;
import models.DefaultErrorMessage;
import models.DefaultSuccessMessage;
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
        return insertAccount(account);
    }


    private Result insertAccount(Account account){
        try {
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

            }
            else {
                throw new SQLException("Creating account failed, no ID obtained.");
            }

        }catch (SQLException e){
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
        }
        return ok(Json.toJson(account));
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

        return updateOneAccount(account);
    }


    private Result updateOneAccount(Account account){
        try {
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

            }
            else {
                throw new SQLException("Updating account failed, no ID obtained.");
            }

        }catch (SQLException e){
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));
            }
        }
        return ok(Json.toJson(account));
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
            connection = db.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM accounts INNER JOIN address ON accounts.address_id = address.address_id WHERE account_id ='"+id+"'").executeQuery();

            if(resultSet.next()){
                Address address = new Address(resultSet.getString("streetname"),resultSet.getString("streetnumber"),resultSet.getInt("zip"),resultSet.getString("city"));
                address.setId(resultSet.getInt("address_id"));
                Account account = new Account(resultSet.getInt("studentid"),resultSet.getString("firstname"),resultSet.getString("lastname"),address,resultSet.getString("email"),resultSet.getString("tel"),resultSet.getString("pw"),resultSet.getBoolean("isadmin"));
                account.setId(resultSet.getInt("account_id"));

                return ok(Json.toJson(account));
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



}
