package pl.beder.bookshelf.mapper;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import pl.beder.bookshelf.controllers.BookController;
import pl.beder.bookshelf.type.Book;

import java.io.InputStream;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;
import static fi.iki.elonen.NanoHTTPD.Method.GET;
import static fi.iki.elonen.NanoHTTPD.Method.POST;
import static fi.iki.elonen.NanoHTTPD.Response.Status.NOT_FOUND;

public class RequestUrlMapper {

    BookController controller = new BookController();

    //    GET /books/1 --> {"id" : 1, "title": "jakaś książka"}
    //    GET /books  --> { {K1}, {K2}, {K3}}
    //    POST /books {K1} --> BookStorage.add({K1})
    public NanoHTTPD.Response delegateRequest(IHTTPSession session) {
        NanoHTTPD.Method method = session.getMethod();
        String uri = session.getUri();

        if (GET == method) {
            if (isCollectionQuery(uri)) {
                return controller.getBooks();
            } else {
                return controller.getBook(session);
            }

        } else if (POST == method) {
            return controller.addBook(session);
        }

        return NanoHTTPD.newFixedLengthResponse(NOT_FOUND, MIME_PLAINTEXT, "Not Found");

    }

    private boolean isCollectionQuery(String uri) {
        return "/books".equals(uri);
    }

}
