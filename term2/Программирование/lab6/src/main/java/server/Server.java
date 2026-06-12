package server;

import model.LabWork;
import utility.CommandManager;
import utility.ScannerManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.*;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class Server {
    private final ScannerManager scannerManager;
    private final CommandManager commandManager;


    private int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    ExecutorService executorService1 = Executors.newCachedThreadPool();

    ExecutorService executorService2 = Executors.newFixedThreadPool(10);
    public ScannerManager getScannerManager() {
        return scannerManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Server(int port, TreeSet<LabWork> treeSet, String path) {
        this.port = port;
        this.scannerManager = new ScannerManager();
        this.commandManager = new CommandManager(treeSet, scannerManager, path);
    }

    public void start() {
        initServerSocketChannel();

        // Main server loop
        while (!Thread.currentThread().isInterrupted()) {
            try {
                selector.select();  // Blocks until events occur or until the reading thread wakes up to add an interesting key
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isAcceptable()) {
                    handleAccept();
                } else if (key.isReadable()) {
                    // Read events are handled asynchronously by submitting tasks to the thread pool
                    key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                    forkJoinPool.invoke(new RequestReader(key, this, executorService1, executorService2));
                }
            }
            selector.selectedKeys().clear(); // Clears processed keys
        }
    }



    private void initServerSocketChannel() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles an incoming connection request
     */
    private void handleAccept() {
        try {
            SocketChannel client = serverSocketChannel.accept();
            if (client != null) {
                // Configure the client channel as non-blocking and register it with the selector for read events
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
