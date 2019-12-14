package pl.beder.bookshelf.repositories;

import pl.beder.bookshelf.type.Book;

import java.util.Collection;
import java.util.Optional;

public interface BookRepository {

    Optional<Book> findById(long id);
    Collection<Book> findAll();
    long addBook(Book bookToAdd);

}
