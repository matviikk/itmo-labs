package server;

import utility.Response;
import utility.Runner;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ResponseSender implements Runnable {
    private final Server server;
    private final Response response;
    private final SelectionKey key;

    public ResponseSender(Server server, Response response, SelectionKey key) {
        this.server = server;
        this.response = response;
        this.key = key;
    }


    public void send(Response response) throws IOException {

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos)) {
//            objectOutputStream.writeObject(response);
//        }
//        byte[] objectBytes = baos.toByteArray();
//
//        SocketChannel channel = (SocketChannel) key.channel();
//
//        // Переключаем канал в блокирующий режим
//        channel.configureBlocking(true);
//
//        // Получаем OutputStream из канала
//        ObjectOutputStream oos = new ObjectOutputStream(channel.socket().getOutputStream());
//
//        // Возвращаем канал в неблокирующий режим, если нужно
//
//        DataOutputStream dataOutputStream = new DataOutputStream(oos);
//        dataOutputStream.writeInt(objectBytes.length);
//        dataOutputStream.write(objectBytes);
//
//
//        channel.configureBlocking(false);

        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] responseBytes = byteArrayOutputStream.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(responseBytes);
        while (buffer.hasRemaining()) {
            try {
                clientSocketChannel.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("RESPONSE SENDER LEVEL");
//            System.out.println("DELAY 3s");
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            send(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
