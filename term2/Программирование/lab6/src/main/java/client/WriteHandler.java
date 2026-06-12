package client;

import model.LabWork;
import utility.Builder;
import utility.CommandManager;
import utility.Request;
import utility.ScannerManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class WriteHandler {
    private final ClientRunner clientRunner;
    private Console console;
    public WriteHandler(ClientRunner clientRunner, BlockingQueue<Request> inputQueue, Console console) {
        this.clientRunner = clientRunner;
        this.console = console;
    }

    public void handle(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        // Проверка наличия данных для записи

        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        if (line.equals("exit")) {
            clientRunner.setRunning(false);
            return;
        }
        if (line.equals("auth")) {
            clientRunner.auth();
            System.out.print("$ ");
            return;
        }
        LabWork labWork = null;
        if (CommandManager.commandsWithLabWorkAsArgument.contains(line.split(" ")[0])) {
            labWork = new Builder(new ScannerManager()).parseLabWork();
        }
        Request request = new Request(line, labWork, clientRunner.login, clientRunner.password);
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
