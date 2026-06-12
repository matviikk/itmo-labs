package Commands;

import model.LabWork;
import utility.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import utility.ScannerManager;
/**
 * Команда для удаления из коллекции всех элементов, которые меньше заданного элемента.
 */
public class RemoveLower extends Command {
    private final TreeSet<LabWork> treeSet;
    private final ScannerManager scannerManager;
    /**
     * Конструктор класса RemoveLower.
     * @param scannerManager Менеджер сканера, предоставляющий методы для чтения ввода пользователя.
     * @param treeSet Коллекция лабораторных работ, из которой будут удаляться элементы.
     */
    public RemoveLower(ScannerManager scannerManager, TreeSet<LabWork> treeSet) {
        super("remove_lower {element}", "удалить из коллекции все элементы, меньшие, чем заданный");
        this.scannerManager = scannerManager;
        this.treeSet = treeSet;
    }
    /**
     * Удаляет все элементы коллекции, которые меньше заданного элемента.
     * @param labWork Элемент, с которым сравниваются элементы коллекции.
     */
    public void removeLower(LabWork labWork){
        List<LabWork> temp = new ArrayList<>();
        for (LabWork lb: treeSet){
            if (labWork.compareTo(lb) < 0){
                temp.add(lb);
            }
        }
        for (LabWork lb: temp){
            treeSet.remove(lb);
        }
    }
    /**
     * Выполняет команду удаления всех элементов коллекции, меньших заданного элемента.
     * @param args Аргументы команды (не используются).
     */
    @Override
    public void execute(String... args) {
        LabWork labWork = new Builder(scannerManager).parseLabWork();
        removeLower(labWork);
    }
}
