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
import java.util.concurrent.BlockingQueue;


public class ReadHandler {

    private Console console;
    private int objectSize = -1;
    private ByteBuffer dataBuffer;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private  BlockingQueue<Request> inputQueue;

    public ReadHandler(BlockingQueue<Request> inputQueue, Console console) {
        this.console = console;
        this.inputQueue = inputQueue;
    }

    public void handle(SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        if (objectSize == -1){
            ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
            socketChannel.read(sizeBuffer);
            sizeBuffer.flip();
            int dataSize = sizeBuffer.getInt();
            objectSize = dataSize;
            dataBuffer = ByteBuffer.allocate(dataSize);
        }
        else{
            socketChannel.read(dataBuffer);
            if (!dataBuffer.hasRemaining()){
                byte[] bytes = new byte[dataBuffer.position()];
                dataBuffer.flip();
                dataBuffer.get(bytes);
                baos.write(bytes);

                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                Response response = (Response)  new ObjectInputStream(bais).readObject();

                if (response != null) {
                    if (response.getMessage() != null) console.println(response.getMessage().trim());
                    if (response.getErrorMessage() != null) console.println("\u001B[31mError: "+ response.getErrorMessage() + " \u001B[0m");
                }

                baos.reset();
                dataBuffer.clear();
                objectSize = -1;
                socketChannel.register(key.selector(), SelectionKey.OP_WRITE);
                console.print("$ ");
            }
        }
    }
}
