package models;


import java.sql.Date;

public class Purchase {

    private Integer id;
    private Integer articleId;
    private Integer buyerId;
    private Boolean isCompleted;
    private Date purchaseDate;

    public Purchase(Integer articleId, Integer buyerId, Boolean isCompleted, Date purchaseDate) {
        this.articleId = articleId;
        this.buyerId = buyerId;
        this.isCompleted = isCompleted;
        this.purchaseDate = purchaseDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

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
