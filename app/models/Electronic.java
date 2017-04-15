package models;


import java.sql.Date;

public class Electronic extends Article {

    private String producer;
    private Integer modelNr;

    public Electronic(String name, Integer price, Integer condition, String description, Date creationDate, String producer, Integer modelNr) {
        super(name, price, condition, description, creationDate);
        this.producer = producer;
        this.modelNr = modelNr;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Integer getModelNr() {
        return modelNr;
    }

    public void setModelNr(Integer modelNr) {
        this.modelNr = modelNr;
    }
}
