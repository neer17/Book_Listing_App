package com.example.neerajsewani.book_listing_app;

public class BooksDetails {
    private String title , author, id;

    BooksDetails(String id, String title, String author){
        this.id  = id;
        this.author = author;
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
