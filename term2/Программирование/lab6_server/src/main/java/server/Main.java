package server;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.LabWork;
import utility.Request;
import utility.Response;

import java.io.*;
import java.util.Scanner;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        String path = args[0];
        TreeSet<LabWork> treeSet = new TreeSet<>();
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        try {
            Scanner fileScanner = new Scanner(new File(path));
            int maxId = 0;
            while (fileScanner.hasNext()) {
                LabWork lb = xmlMapper.readValue(fileScanner.nextLine(), LabWork.class);
                treeSet.add(lb);
                maxId = Math.max(maxId, lb.getId());
            }
            LabWork.setGeneratedId(maxId + 1);
        } catch (Exception e) {
            System.out.println("\u001B[31mError: Ошибка при запуске программы \u001B[0m");
        }

        Server server = new Server(1234, treeSet, path);
        ConnectionReceiver connectionReceiver = new ConnectionReceiver(server);
        RequestReader requestReader = new RequestReader(server);
        RequestHandler requestHandler = new RequestHandler(server);

        ResponseSender responseSender = new ResponseSender(server);

        while (true){
            connectionReceiver.acceptConnection();
            while (true){
                try {
                    Request request = requestReader.read();
                    Response response = requestHandler.handle(request);
                    responseSender.send(response);
                } catch (IOException e) {
                    System.out.println("Соединение потеряно, ждем нового подключения");
                    connectionReceiver.acceptConnection();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
