package models;

import java.sql.Date;

public class Book extends Article {

    private String isbn;
    private String author;
    private String publisher;


    public Book(Integer id, String name, Integer price, Integer condition, String description, Date creationDate, String isbn, String author, String publisher) {
        super(id, name, price, condition, description, creationDate);
        this.isbn = isbn;
        this.author = author;
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() { return publisher; }

    public void setPublisher(String publisher) { this.publisher = publisher; }
}
