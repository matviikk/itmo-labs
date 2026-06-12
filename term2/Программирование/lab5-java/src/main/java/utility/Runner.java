package utility;

import Commands.Save;
import model.LabWork;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.TreeSet;

public class Runner {
    /**
     * Набор всех лабораторных работ, упорядоченных по их уникальным идентификаторам.
     */
    private final TreeSet<LabWork> treeSet;
    /**
     * История введенных команд.
     */
    private final Deque<String> history = new ArrayDeque<>();
    /**
     * Менеджер для обработки ввода от пользователя.
     */
    private final ScannerManager scannerManager;
    /**
     * Флаг, указывающий на то, продолжается ли выполнение программы.
     */
    private boolean isRunning = true;
    /**
     * Менеджер команд, отвечающий за исполнение доступных команд.
     */
    private final CommandManager commandManager;
    private final String path;

    public Runner(TreeSet<LabWork> treeSet, String path) {
        this.treeSet = treeSet;
        this.scannerManager = new ScannerManager();
        this.commandManager = new CommandManager(treeSet, scannerManager, path, this);
        this.path = path;
    }

    public void run(){
        if (!scannerManager.isReadingFile) {
            System.out.println("Введите \"help\" для справки по командам.");
        }
        while (isRunning) {
            System.out.print("$ ");
            try {
                if (!scannerManager.hasNextLine()) {
                    saveAndExit();
                }
                String string = scannerManager.nextLine();
                history.addFirst(string.split(" ")[0]);
                if (history.size() > 9) {
                    history.removeLast();
                }
                commandManager.executeCommand(string);
            } catch (Exception e) {
                System.out.println("\u001B[31mError: Ошибка ввода. \u001B[0m");
                System.out.println("\u001B[31mError: Экстренное завершение программы... \u001B[0m");
                return;
            }
        }
    }
    /**
     * Выполняет сохранение и выход из программы, при вызове исключения.
     */
    public void saveAndExit() {
        new Save(treeSet, path).save();
        System.out.println("Data saved successfully. Exiting program...");
        System.exit(0);
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public Deque<String> getHistory() {
        return history;
    }
}
