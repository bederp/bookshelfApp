package pl.beder.bookshelf.type;


import lombok.Data;

@Data
public class Book {
    private long id;
    private String title;
    private String author;
    private int numberOfPages;
    private int yearOfPublication;
    private String publishingHouse;
}
