package models;


import java.sql.Date;

public class Electronic extends Article {

    private String producer;
    private String model;

    public Electronic(String name, Double price, Integer condition, String description, Date creationDate,  String image, String type, String producer, String model) {
        super(name, price, condition, description, creationDate, image, type);
        this.producer = producer;
        this.model = model;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
