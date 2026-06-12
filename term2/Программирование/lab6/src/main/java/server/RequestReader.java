package server;

import utility.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveTask;

public class RequestReader extends RecursiveTask<Boolean> {
    private SelectionKey key;
    private ExecutorService service1;
    private ExecutorService service2;
    private Server server;

    public RequestReader(SelectionKey key, Server server, ExecutorService executorService1, ExecutorService executorService2) {
        this.key = key;
        this.server = server;
        this.service1 = executorService1;
        this.service2 = executorService2;
    }

    @Override
    public Boolean compute() {
        System.out.println("REQUEST READER LEVEL");
//        System.out.println("DELAY 3s");
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        SocketChannel channel = (SocketChannel) key.channel();


//        Request request = null;
//        try {
//            // Переключаем канал в блокирующий режим
//            channel.configureBlocking(true);
//
//            // Получаем InputStream из канала
//            ObjectInputStream clientInput = new ObjectInputStream(channel.socket().getInputStream());
//
//            request = (Request) clientInput.readObject();
//            // Возвращаем канал в неблокирующий режим, если нужно
//            channel.configureBlocking(false);
//        }
//        catch (RuntimeException | IOException | ClassNotFoundException e){
//            e.printStackTrace();
//        }
//
//        System.out.println("request: " + request);

        ByteBuffer buffer = ByteBuffer.allocate(8192);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        try {
            while ((bytesRead = channel.read(buffer)) > 0) {
                buffer.flip();
                byteArrayOutputStream.write(buffer.array(), 0, buffer.limit());
                buffer.clear();
            }
            if (bytesRead == -1) {
                // Connection closed by client
                key.cancel();
                channel.close();
                return false;
            }
        } catch (IOException e) {
            key.cancel();
            try {
                channel.close();
            } catch (IOException ce) {
                ce.printStackTrace();
            }
            return false;
        }


        service1.submit(new RequestHandler(server, byteArrayOutputStream, service2, key));


        key.interestOps(key.interestOps() | SelectionKey.OP_READ);
        // Wake up the selector to update interest operations
        key.selector().wakeup();

        return true;
    }
}
