package models;


import java.sql.Date;

public class OfficeSupply extends Article {

    public OfficeSupply(String name, Integer price, Integer condition, String description, Date creationDate, String image) {
        super(name, price, condition, description, creationDate, image);
    }
}
