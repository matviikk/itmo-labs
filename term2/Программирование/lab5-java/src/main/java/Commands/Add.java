package Commands;

import model.LabWork;

import java.util.TreeSet;
import utility.Builder;
import utility.ScannerManager;
/**
 * Команда для добавления новой лабораторной работы в коллекцию.
 */
public class Add extends Command {
    private final ScannerManager scannerManager; // Менеджер для обработки ввода пользователя
    private final TreeSet<LabWork> treeSet; // Коллекция для хранения лабораторных работ
    /**
     * Конструктор класса Add.
     * @param scannerManager Менеджер сканера, предоставляющий методы для чтения ввода пользователя.
     * @param treeSet Коллекция, в которую будет добавлена новая лабораторная работа.
     */
    public Add(ScannerManager scannerManager, TreeSet<LabWork> treeSet) {
        super("add {element}", "добавить новый элемент в коллекцию");
        this.scannerManager = scannerManager;
        this.treeSet = treeSet;
    }
    /**
     * Добавляет лабораторную работу в коллекцию.
     * @param labWork Лабораторная работа для добавления.
     */
    public void add(LabWork labWork){
        treeSet.add(labWork);
    }
    /**
     * Выполняет команду добавления лабораторной работы, парся данные ввода пользователя.
     * @param args Аргументы команды (не используются).
     */
    @Override
    public void execute(String... args) {
        LabWork labWork = new Builder(scannerManager).parseLabWork();
        add(labWork);
    }
}
