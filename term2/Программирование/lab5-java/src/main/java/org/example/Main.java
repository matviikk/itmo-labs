package org.example;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.*;
import utility.Runner;

import java.io.*;
import java.util.*;

/**
 * Основной класс приложения, управляющий загрузкой данных и выполнением команд.
 */
public class Main {
    /**
     * Точка входа в программу. Инициализирует обработку входных данных и управляет основным циклом выполнения команд.
     *
     * @param args Аргументы командной строки, первый из которых ожидается как путь к файлу данных.
     */
    public static void main(String[] args) {
        String path = args[0];
        TreeSet<LabWork> treeSet = new TreeSet<>();
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        try {
            Scanner fileScanner = new Scanner(new File(path));
            int maxId = -1;
            while (fileScanner.hasNext()) {
                LabWork lb = xmlMapper.readValue(fileScanner.nextLine(), LabWork.class);
                treeSet.add(lb);
                maxId = Math.max(maxId, lb.getId());
            }
            LabWork.setGeneratedId(maxId + 1);
        } catch (Exception e) {
            System.out.println("\u001B[31mError: Ошибка при запуске программы \u001B[0m");
        }
        Runner runner = new Runner(treeSet, path);
        runner.run();
    }
}