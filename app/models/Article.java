package models;

import java.sql.Date;

public class Article {

    private Integer id;
    private String name;
    private Double price;
    private Integer condition;
    private String description;
    private Date creationDate;
    private String image;
    private String type;

    public Article(String name, Double price, Integer condition, String description, Date creationDate, String image, String type) {
        this.name = name;
        this.price = price;
        this.condition = condition;
        this.description = description;
        this.creationDate = creationDate;
        this.image = image;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
