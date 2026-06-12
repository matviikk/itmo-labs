package client;

import utility.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

public class WriteHandler {
    private BlockingQueue<Request> inputQueue;
    private Console console;
    public WriteHandler(BlockingQueue<Request> inputQueue, Console console) {
        this.inputQueue = inputQueue;
        this.console = console;
    }

    public void handle(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        // Проверка наличия данных для записи
        if (!inputQueue.isEmpty()) {

            Request request = inputQueue.poll();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(request);
            oos.flush();
            byte[] bytes = baos.toByteArray();
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            while (byteBuffer.hasRemaining()) {
                socketChannel.write(byteBuffer);
            }
            socketChannel.register(key.selector(), SelectionKey.OP_READ);
        }
    }
}
