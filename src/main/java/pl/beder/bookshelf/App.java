package pl.beder.bookshelf;

import fi.iki.elonen.NanoHTTPD;
import pl.beder.bookshelf.mapper.RequestUrlMapper;

import java.io.IOException;

public class App extends NanoHTTPD {

    private RequestUrlMapper mapper = new RequestUrlMapper();

    public App(int port) {
        super(port);
    }

    public static void main(String[] args) throws IOException {
        App app = new App(8080);
        app.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

    }

    @Override
    public Response serve(IHTTPSession session) {
        return mapper.delegateRequest(session);
    }


}
