package server;

import utility.Request;
import utility.Response;

import java.io.*;

public class ResponseSender {
    private final Server server;

    public ResponseSender(Server server) {
        this.server = server;
    }


    public void send(Response response) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos)) {
            objectOutputStream.writeObject(response);
        }
        byte[] objectBytes = baos.toByteArray();

        DataOutputStream dataOutputStream = new DataOutputStream(server.getClient().getOutputStream());
        dataOutputStream.writeInt(objectBytes.length);
        dataOutputStream.write(objectBytes);
    }
}
