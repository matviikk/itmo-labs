package server;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.LabWork;
import utility.Request;
import utility.Response;

import java.io.*;
import java.util.Scanner;
import java.util.TreeSet;

import static utility.DatabaseManager.createDatabaseIfNotExists;

public class Main {
    public static void main(String[] args) {
        String path = "";
        TreeSet<LabWork> treeSet = new TreeSet<>();

        Server server = new Server(1234, treeSet, path);


        createDatabaseIfNotExists();

        server.start();


    }
}
