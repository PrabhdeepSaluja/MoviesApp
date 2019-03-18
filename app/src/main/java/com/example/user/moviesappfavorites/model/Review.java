package com.example.user.moviesappfavorites.model;

public class Review {
    private String author;
    private String content;

    // constructor
    public Review(){
    }

    public Review(String author, String content){
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}