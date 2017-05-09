package models;


import java.sql.Date;

public class OtherArticle extends Article {

    public OtherArticle(String name, Double price, Integer condition, String description, Date creationDate, String image, String type) {
        super(name, price, condition, description, creationDate, image, type);
    }
}
