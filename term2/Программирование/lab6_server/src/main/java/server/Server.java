package server;

import model.LabWork;
import utility.CommandManager;
import utility.ScannerManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeSet;

public class Server {
    private final ScannerManager scannerManager;
    private final CommandManager commandManager;
    private Socket client;
    private ServerSocket serverSocket;

    public ScannerManager getScannerManager() {
        return scannerManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Server(int port, TreeSet<LabWork> treeSet, String path) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.scannerManager = new ScannerManager();
        this.commandManager = new CommandManager(treeSet, scannerManager, path);
    }


    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

}
