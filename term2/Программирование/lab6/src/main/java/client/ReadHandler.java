package client;

import utility.Request;
import utility.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;


public class ReadHandler {

    private final ClientRunner clientRunner;
    private Console console;
    private int objectSize = -1;
    private ByteBuffer dataBuffer;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private  BlockingQueue<Request> inputQueue;

    public ReadHandler(ClientRunner clientRunner, BlockingQueue<Request> inputQueue, Console console) {
        this.clientRunner = clientRunner;
        this.console = console;
        this.inputQueue = inputQueue;
    }

    public void handle(SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(16384);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < 10000) {
                if (key.isReadable()) {
                    int bytesRead;
                    while ((bytesRead = socketChannel.read(buffer)) > 0) {
                        buffer.flip();
                        byteArrayOutputStream.write(buffer.array(), 0, bytesRead);
                        buffer.clear();
                    }
                    if (bytesRead == -1) {
                        // Закрытие канала
                        socketChannel.close();
                    }
                }
                byte[] responseBytes = byteArrayOutputStream.toByteArray();
                if (responseBytes.length > 0) {
                    try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(responseBytes))) {
                        Response response = (Response) objectInputStream.readObject();
                        if (response != null) {

                            if (response.getMessage() != null) console.println(response.getMessage().trim());
                            if (response.getErrorMessage() != null) console.println("\u001B[31mError: "+ response.getErrorMessage() + " \u001B[0m");
                            //if (response.isNeedAuth()) clientRunner.auth();
                        }
                        break;
                    } catch (Exception ignore) {
                    }
                }
            }

        socketChannel.register(key.selector(), SelectionKey.OP_WRITE);
        console.print("$ ");
    }

}
