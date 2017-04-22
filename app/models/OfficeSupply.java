package models;


import java.sql.Date;

public class OfficeSupply extends Article {

    public OfficeSupply(String name, Integer price, Integer condition, String description, Date creationDate, String image, String type) {
        super(name, price, condition, description, creationDate, image, type);
    }
}
