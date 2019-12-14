package pl.beder.bookshelf.repositories;

import pl.beder.bookshelf.type.Book;

import java.util.*;

public class HashMapBookRepository implements BookRepository {

    private Map<Long, Book> repo = new HashMap<>();


    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(repo.get(id));
    }

    @Override
    public Collection<Book> findAll() {
        return repo.values();
    }

    @Override
    public long addBook(Book bookToAdd) {
        long id = UUID.randomUUID().getLeastSignificantBits();
        bookToAdd.setId(id);
        repo.put(id, bookToAdd);
        return id;
    }
}
