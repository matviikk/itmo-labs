package Commands;

import model.LabWork;
import utility.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import utility.Request;
import utility.Response;
import utility.ScannerManager;
/**
 * Команда для удаления из коллекции всех элементов, которые превышают заданный элемент.
 */
public class RemoveGreater extends Command {
    private final TreeSet<LabWork> treeSet;
    private final ScannerManager scannerManager;
    /**
     * Конструктор класса RemoveGreater.
     * @param scannerManager Менеджер сканера, предоставляющий методы для чтения ввода пользователя.
     * @param treeSet Коллекция лабораторных работ, из которой будут удаляться элементы.
     */
    public RemoveGreater(ScannerManager scannerManager, TreeSet<LabWork> treeSet) {
        super("remove_greater {element}", "удалить из коллекции все элементы, превышающие заданный");
        this.scannerManager = scannerManager;
        this.treeSet = treeSet;
    }
    /**
     * Удаляет все элементы коллекции, которые больше заданного элемента.
     * @param labWork Элемент, с которым сравниваются элементы коллекции.
     */
    public void removeGreater(LabWork labWork){
        List<LabWork> temp = new ArrayList<>();
        for (LabWork lb: treeSet){
            if (labWork.compareTo(lb) > 0){
                temp.add(lb);
            }
        }
        for (LabWork lb: temp){
            treeSet.remove(lb);
        }
    }
    /**
     * Выполняет команду удаления всех элементов коллекции, превышающих заданный элемент.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        LabWork labWork = request.getLabWork();
        removeGreater(labWork);
        return new Response("Success");
    }
}
