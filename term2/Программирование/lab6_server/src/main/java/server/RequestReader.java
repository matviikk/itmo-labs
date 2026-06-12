package server;

import utility.Request;

import java.io.IOException;
import java.io.ObjectInputStream;

public class RequestReader {
    private final Server server;

    public RequestReader(Server server) {
        this.server = server;
    }
    public Request read() throws IOException, ClassNotFoundException {
        ObjectInputStream clientInput = new ObjectInputStream(server.getClient().getInputStream());
        Request request = (Request) clientInput.readObject();
        System.out.println("request: " + request);
        return request;
    }

}
