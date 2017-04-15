package models;


import java.sql.Date;

public class OfficeSupply extends Article {

    public OfficeSupply(String name, Integer price, Integer condition, String description, Date creationDate) {
        super(name, price, condition, description, creationDate);
    }
}
