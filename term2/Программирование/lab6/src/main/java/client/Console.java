package client;

import model.LabWork;
import utility.Builder;
import utility.CommandManager;
import utility.Request;
import utility.ScannerManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class Console {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private ClientRunner clientRunner;
    private BlockingQueue<Request> inputQueue;
    public Console(ClientRunner clientRunner, BlockingQueue<Request> inputQueue) {
        this.clientRunner = clientRunner;
        this.inputQueue = inputQueue;

//        new Thread(() -> {
//            var reader = new BufferedReader(new InputStreamReader(System.in));
//            while (clientRunner.isRunning()) {
//                try {
//                    String line = reader.readLine();
//                    if (line.equals("exit")) {
//                        clientRunner.setRunning(false);
//                        continue;
//                    }
//                    LabWork labWork = null;
//                    if (CommandManager.commandsWithLabWorkAsArgument.contains(line.split(" ")[0])) {
//                        labWork = new Builder(new ScannerManager()).parseLabWork();
//                    }
//                    Request request = new Request(line, labWork, clientRunner.login, clientRunner.password);
//                    inputQueue.put(request);
//                } catch (IOException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

    }

    public void print(Object o){
        System.out.print(o);
    }
    public void println(Object o){
        System.out.println(o);
    }

}
