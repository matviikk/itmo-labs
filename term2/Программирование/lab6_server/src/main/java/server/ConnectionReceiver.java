package server;

import java.io.IOException;

public class ConnectionReceiver {
    private final Server server;

    public ConnectionReceiver(Server server) {
        this.server = server;
    }

    public void acceptConnection(){
        try {
            System.out.println("Ожидание подключения");
            server.setClient(server.getServerSocket().accept());
            System.out.println("Подключен к: " + server.getClient().getInetAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
