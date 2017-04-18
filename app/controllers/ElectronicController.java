package controllers;

import models.DefaultErrorMessage;
import models.Electronic;
import play.db.Database;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ElectronicController extends Controller{

    private Database db;

    @Inject
    public ElectronicController(Database db) {
        this.db = db;
    }

    public Result getAllElectronics(){
        try (Connection connection = db.getConnection()){

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN electronics on articles.article_id = electronics.electronic_id;").executeQuery();
            ArrayList<Electronic> list = new ArrayList<>();

            while(resultSet.next()){
                Electronic electronic = new Electronic(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"),resultSet.getString("manufacturer"),resultSet.getString("modell"));
                electronic.setId(resultSet.getInt("article_id"));
                list.add(electronic);
            }

            return ok(Json.toJson(list));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }
    }

}
