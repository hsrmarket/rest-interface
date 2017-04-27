package controllers;


import play.db.Database;
import play.mvc.Controller;

import javax.inject.Inject;
import java.sql.Connection;

public class PurchaseController extends Controller {

    private Database db;
    private Connection connection;

    @Inject
    public PurchaseController(Database db) {
        this.db = db;
    }

    

}
