package controllers;

import models.DefaultErrorMessage;
import models.OfficeSupply;
import play.db.Database;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class OfficeSupplyController extends Controller {

    private Database db;

    @Inject
    public OfficeSupplyController(Database db) {
        this.db = db;
    }


    public Result getAllOfficeSupplies(){

        try (Connection connection = db.getConnection()){

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN officesupplies on articles.article_id = officesupplies.officesupplie_id;").executeQuery();
            ArrayList<OfficeSupply> list = new ArrayList<>();

            while(resultSet.next()){
                OfficeSupply officeSupply = new OfficeSupply(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"));
                officeSupply.setId(resultSet.getInt("article_id"));
                list.add(officeSupply);
            }

            return ok(Json.toJson(list));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }
    }

}
