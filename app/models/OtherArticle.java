package models;


import java.sql.Date;

public class OtherArticle extends Article {

    public OtherArticle(String name, Integer price, Integer condition, String description, Date creationDate, String image) {
        super(name, price, condition, description, creationDate, image);
    }
}
