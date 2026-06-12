package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ConnectionMonitor {

    private final String ADDRESS;
    private final int PORT;
    private SocketChannel clientSocketChannel;
    private Selector selector;

    public Selector getSelector() {
        return selector;
    }


    public ConnectionMonitor(String address, int port) throws IOException {
        ADDRESS = address;
        PORT = port;
        this.selector = Selector.open();;
    }
    public void connect() throws IOException {
        SocketAddress inetSocketAddress = new InetSocketAddress(ADDRESS, PORT);
        clientSocketChannel = SocketChannel.open();
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.connect(inetSocketAddress);
        clientSocketChannel.register(selector, SelectionKey.OP_CONNECT);

    }
    public void handle(SelectionKey key) throws IOException {
        if (clientSocketChannel.finishConnect()) {
            clientSocketChannel.register(key.selector(), SelectionKey.OP_WRITE);
        }
    }
}
