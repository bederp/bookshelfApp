package pl.beder.bookshelf.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;
import lombok.SneakyThrows;
import pl.beder.bookshelf.repositories.BookRepository;
import pl.beder.bookshelf.repositories.HashMapBookRepository;
import pl.beder.bookshelf.type.Book;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;
import static fi.iki.elonen.NanoHTTPD.Response.Status.*;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

public class BookController {

    private static final String APPLICATION_JSON = "application/json";

    private BookRepository repository = new HashMapBookRepository();
    private ObjectMapper mapper = new ObjectMapper();

    public NanoHTTPD.Response getBooks() {
        Collection<Book> books = repository.findAll();
        try {
            return NanoHTTPD.newFixedLengthResponse(OK, APPLICATION_JSON, mapper.writeValueAsString(books));
        } catch (JsonProcessingException e) {
            return newFixedLengthResponse(INTERNAL_ERROR, MIME_PLAINTEXT, "Error serializing books to json");


        }
    }

    public NanoHTTPD.Response getBook(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri();
        String idAsString = uri.replace("/books/", "");
        long id;
        try {
            id = Long.parseLong(idAsString);
        } catch (NumberFormatException e) {
            return newFixedLengthResponse(BAD_REQUEST, MIME_PLAINTEXT, "Book id is required in url and needs to be a number");
        }
        Optional<Book> book = repository.findById(id);

        return book.map(this::toResponse).orElse(bookNotFound(id));
    }

    private NanoHTTPD.Response bookNotFound(long id) {
        return newFixedLengthResponse(NOT_FOUND, MIME_PLAINTEXT, "Book with id:"+ id + " not found");
    }

    private NanoHTTPD.Response toResponse(Book book) {
        try {
            return NanoHTTPD.newFixedLengthResponse(OK, APPLICATION_JSON, mapper.writeValueAsString(book));
        } catch (JsonProcessingException e) {
            return newFixedLengthResponse(INTERNAL_ERROR, MIME_PLAINTEXT,
                    "Error serializing book with Id:" + book.getId() + " to json");
        }
    }

    public NanoHTTPD.Response addBook(NanoHTTPD.IHTTPSession session) {
        try {
            Book book = mapper.readValue(getBody(session), Book.class);
            long bookId = repository.addBook(book);
            return newFixedLengthResponse(CREATED, APPLICATION_JSON, "Created book with Id:" +bookId);
        } catch (IOException | NanoHTTPD.ResponseException e) {
            return newFixedLengthResponse(INTERNAL_ERROR, MIME_PLAINTEXT, "Error deserializing books to json");
        }
    }

    private String getBody(NanoHTTPD.IHTTPSession session) throws IOException, NanoHTTPD.ResponseException {
        final HashMap<String, String> map = new HashMap<>();
        session.parseBody(map);
        return map.get("postData");
    }
}
