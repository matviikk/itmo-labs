package client;

import utility.Request;
import utility.Response;
import utility.Runner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientRunner implements Runner {
    private BlockingQueue<Request> inputQueue = new LinkedBlockingQueue<>();
    private boolean isRunning = true;
    private ConnectionMonitor connectionMonitor;
    protected String login;
    protected String password;
    private Console console;

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
            console = new Console(this, inputQueue);
            ReadHandler readHandler = new ReadHandler(this, inputQueue, console);
            WriteHandler writeHandler = new WriteHandler(this, inputQueue, console);
            System.out.println();


            connectionMonitor.connect();// начали подключаться

            auth();

            System.out.println("Введите \"auth\" для ввода логина и пароля.");
            System.out.println("Введите \"registration\" для регистрации текущего пользователя");
            System.out.println("Введите \"help\" для справки по командам.");

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
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        socketChannel.register(key.selector(), SelectionKey.OP_CONNECT);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
    public void auth(){
        Scanner scanner = new Scanner(System.in);
        console.print("Введите логин: ");
        this.login = scanner.nextLine();
        console.print("Введите пароль: ");
        this.password = scanner.nextLine();
    }
}
