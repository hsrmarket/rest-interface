package models;


import java.sql.Date;

public class Purchase {

    private Integer id;
    private Article article;
    private Account buyer;
    private Account seller;
    private Boolean isCompleted;
    private Date purchaseDate;

    public Purchase(Article article, Account buyer, Boolean isCompleted, Date purchaseDate, Account seller) {
        this.article = article;
        this.buyer = buyer;
        this.isCompleted = isCompleted;
        this.purchaseDate = purchaseDate;
        this.seller = seller;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Account getBuyer() {
        return buyer;
    }

    public void setBuyer(Account buyer) {
        this.buyer = buyer;
    }

    public Account getSeller() {return seller;}

    public void setSeller(Account seller) {this.seller = seller;}

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
