package models;


import java.sql.Date;

public class Other extends Article {

    public Other(String name, Integer price, Integer condition, String description, Date creationDate) {
        super(name, price, condition, description, creationDate);
    }
}
