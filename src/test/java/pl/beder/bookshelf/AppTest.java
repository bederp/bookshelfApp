package pl.beder.bookshelf;

import fi.iki.elonen.NanoHTTPD;
import io.restassured.RestAssured;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.*;

class AppTest {

    private static final int TESTING_PORT = 8090;
    private App app;

    @BeforeAll
    static void beforeAll() {
        RestAssured.port = TESTING_PORT;
    }

    @BeforeEach
    void setUp() throws IOException {
        app = new App(TESTING_PORT);
        app.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @AfterEach
    void tearDown() {
        app.stop();
    }

    @Test
    void shouldAddBooksCorrectlyAndReturnId() {
        RestAssured
                .with()
                .header("Content-Type", "application/json; charset=utf-8")
                .body(getExampleBook())
                .when()
                .post("/books")
                .then()
                .statusCode(201)
                .body(startsWith("Created book with Id:"));
    }

    @Test
    void incorrectBookDefinitionShouldReturnServerError() {
        RestAssured
                .with()
                .header("Content-Type", "application/json; charset=utf-8")
                .body(getIncorrectBook())
                .when()
                .post("/books")
                .then()
                .statusCode(500)
                .body(equalTo("Error deserializing books to json"));
    }

    @Test
    void persistedBookShouldBeRetrievable() {
        long bookId = persistBookAndReturnId();

        RestAssured
                .when()
                .get("/books/" + bookId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Krzyżacy"));
    }

    @Test
    void missingBookIdWhenGettingBookShouldReturnClientError() {
        RestAssured
                .with()
                .header("Content-Type", "application/json; charset=utf-8")
                .body(getIncorrectBook())
                .when()
                .get("/books/")
                .then()
                .statusCode(400)
                .body(startsWith("Book id is required"));
    }

    @Test
    void bookIdThatIsNotANumberWhenGettingBookShouldReturnClientError() {
        RestAssured
                .with()
                .header("Content-Type", "application/json; charset=utf-8")
                .body(getIncorrectBook())
                .when()
                .get("/books/blad")
                .then()
                .statusCode(400)
                .body(startsWith("Book id is required"));
    }

    @Test
    void queryingForNonExistingBookShouldReturn404() {
        RestAssured
                .with()
                .header("Content-Type", "application/json; charset=utf-8")
                .body(getIncorrectBook())
                .when()
                .get("/books/123")
                .then()
                .statusCode(404)
                .body(containsString("not found"));
    }

    @Test
    void gettingAllBooksFromFreshServerShouldReturnEmptyArray() {
        RestAssured
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .body("", hasSize(0));
    }

    @Test
    void afterAdding2BooksServerShouldReturnArrayContaining2Books() {
        persistBookAndReturnId();
        persistBookAndReturnId();

        RestAssured
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .body("", hasSize(2));
    }

    private long persistBookAndReturnId() {
        ResponseBody<?> body = RestAssured
                .with()
                .header("Content-Type", "application/json; charset=utf-8")
                .body(getExampleBook())
                .when()
                .post("/books")
                .getBody();
        return parseResponsForBookId(body);
    }

    private long parseResponsForBookId(ResponseBody<?> body) {
        return Long.parseLong(body.asString()
                .replace("Created book with Id:", ""));
    }

    private String getExampleBook() {
        return "{\n" +
                " \n" +
                "  \"title\" : \"Krzyżacy\",\n" +
                "  \"author\" :  \"Sienkiewicz\",\n" +
                "  \"numberOfPages\" : 123,\n" +
                "  \"yearOfPublication\" :  1970,\n" +
                "  \"publishingHouse\" : \"Wydawnictwo powszechne\"\n" +
                "}";
    }

    private String getIncorrectBook() {
        return "{\n" +
                " \n" +
                "  \"title\" : \"Krzyżacy\",\n" +
                "  \"author\" :  \"Sienkiewicz\",\n" +
                "  \"numberOfPages\" : 123,\n" +
                "  \"yearOfPublication\" :  1970 błąd,\n" +
                "  \"publishingHouse\" : \"Wydawnictwo powszechne\"\n" +
                "}";
    }
}