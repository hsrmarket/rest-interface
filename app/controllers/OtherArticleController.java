package controllers;

import models.DefaultErrorMessage;
import models.OtherArticle;
import play.db.Database;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OtherArticleController extends Controller {

    private Database db;

    @Inject
    public OtherArticleController(Database db) {
        this.db = db;
    }


    public Result getAllOtherArticles(){

        try (Connection connection = db.getConnection()){

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM articles INNER JOIN otherarticles on articles.article_id = otherarticles.otherarticle_id").executeQuery();
            ArrayList<OtherArticle> list = new ArrayList<>();

            while(resultSet.next()){
                OtherArticle otherArticle = new OtherArticle(resultSet.getString("name"),resultSet.getInt("price"),resultSet.getInt("condition"),resultSet.getString("description"),resultSet.getDate("creationdate"),resultSet.getString("image"));
                otherArticle.setId(resultSet.getInt("article_id"));
                list.add(otherArticle);
            }

            return ok(Json.toJson(list));

        } catch (SQLException e) {
            return badRequest(Json.toJson(new DefaultErrorMessage(e.getErrorCode(),e.getMessage())));

        }
    }

}
