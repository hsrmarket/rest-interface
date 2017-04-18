package models;


import java.sql.Date;

public class Other extends Article {

    public Other(String name, Integer price, Integer condition, String description, Date creationDate, String image) {
        super(name, price, condition, description, creationDate, image);
    }
}
