package Commands;

import model.LabWork;
import utility.Builder;

import java.util.Objects;
import java.util.TreeSet;
import utility.ScannerManager;
/**
 * Команда для обновления элемента коллекции по его ID.
 */
public class Update extends Command {
    private final ScannerManager scannerManager;
    private final TreeSet<LabWork> treeSet;
    /**
     * Конструктор класса Update.
     * @param scannerManager Менеджер сканера, предоставляющий методы для чтения ввода пользователя.
     * @param treeSet Коллекция лабораторных работ, в которой будет обновлен элемент.
     */
    public Update(ScannerManager scannerManager, TreeSet<LabWork> treeSet) {
        super("update id {element}", "обновить значение элемента коллекции, id которого равен заданному");
        this.scannerManager = scannerManager;
        this.treeSet = treeSet;
    }
    /**
     * Обновляет элемент в коллекции, заменяя его на новый элемент с тем же ID.
     * @param id ID элемента для обновления.
     * @param labWork Новый элемент, который заменит старый.
     */
    public void update(Integer id, LabWork labWork){
        LabWork temp = null;
        for (LabWork lb: treeSet){
            if (Objects.equals(lb.getId(), id)){
                temp = lb;
            }
        }
        treeSet.remove(temp);
        labWork.setId(id);
        treeSet.add(labWork);
    }
    /**
     * Выполняет команду обновления элемента по ID.
     * @param args Аргументы команды, первый аргумент - ID элемента для обновления.
     */
    @Override
    public void execute(String... args) {
        int id = Integer.parseInt(args[0]);
        LabWork element = new Builder(scannerManager).parseLabWork();
        update(id, element);
    }
}
