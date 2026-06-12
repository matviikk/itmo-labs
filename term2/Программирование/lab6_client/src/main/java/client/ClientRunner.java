package client;

import utility.Request;
import utility.Runner;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientRunner implements Runner {
    private BlockingQueue<Request> inputQueue = new LinkedBlockingQueue<>();
    private boolean isRunning = true;
    private ConnectionMonitor connectionMonitor;

    public ClientRunner(String address, int port) {

        try {
            connectionMonitor = new ConnectionMonitor(address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        try {
            Console console = new Console(this, inputQueue);
            ReadHandler readHandler = new ReadHandler(inputQueue, console);
            WriteHandler writeHandler = new WriteHandler(inputQueue, console);

            System.out.println("Введите \"help\" для справки по командам.");

            connectionMonitor.connect();// начали подключаться
            while (isRunning) {
                connectionMonitor.getSelector().select();
                Iterator<SelectionKey> keyIterator = connectionMonitor.getSelector().selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    try {
                        if (key.isConnectable()) {
                            connectionMonitor.handle(key);
                            console.print("$ ");
                        } else if (key.isReadable()) {
                            readHandler.handle(key);
                        } else if (key.isWritable()) {
                            writeHandler.handle(key);
                        }
                    } catch (IOException e) {
                        System.out.println("Ошибка подключения!");
                        Thread.sleep(1000);
                        connectionMonitor.connect();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleConnect(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        if (socketChannel.finishConnect()) {
            socketChannel.register(key.selector(), SelectionKey.OP_WRITE);
        }
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}
