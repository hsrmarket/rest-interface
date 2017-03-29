package controllers;

import play.api.libs.json.Json;
import play.db.Database;
import play.mvc.*;
import views.html.*;

import javax.inject.Inject;


public class BookController extends Controller {

    private Database db;

    @Inject
    public BookController(Database db) {
        this.db = db;
    }

    public Result getAll(){
        db.getConnection();

        return null;
    }

    public Result insert(){

        return ok(index.render("Dataset successfully insert"));
    }
}
