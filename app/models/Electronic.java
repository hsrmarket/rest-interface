package models;


public class Electronic extends Article {

    private String producer;
    private Integer modelNr;


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
