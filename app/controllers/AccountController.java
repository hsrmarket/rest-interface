package controllers;

import models.Account;
import models.Address;
import models.DefaultErrorMessage;
import play.db.Database;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class AccountController extends Controller {

    private Database db;
    private Connection connection;

    @Inject
    public AccountController(Database db) {
        this.db = db;
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
