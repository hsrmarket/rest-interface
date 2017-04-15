package models;

public class DefaultSuccessMessage {

    private Integer id;
    private String message;

    public DefaultSuccessMessage(Integer id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getMessage(){
        return this.message;
    }

    public void setMessage(String message){
        this.message = message;
    }

}
