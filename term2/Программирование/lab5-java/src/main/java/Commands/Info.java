package Commands;

import model.LabWork;

import java.util.Date;
import java.util.TreeSet;
/**
 * Команда для вывода информации о коллекции лабораторных работ.
 */
public class Info extends Command {
    private final TreeSet<LabWork> treeSet;
    private final Save save;
    /**
     * Конструктор команды Info.
     * @param treeSet Коллекция лабораторных работ, для которой будет выводиться информация.
     */

    public Info(TreeSet<LabWork> treeSet, Save save) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        this.treeSet = treeSet;
        this.save = save;
    }
    /**
     * Выполняет команду, выводя информацию о коллекции.
     * @param args Аргументы команды (не используются в этой команде).
     */
    @Override
    public void execute(String... args) {
        System.out.println("Collection type: " + treeSet.getClass().getName());
        System.out.println("Initialization date: " + new Date());
        System.out.println("Collection size: " + treeSet.size());
        if (save.getLastSaveTime() != null) {
            System.out.println("Дата последнего сохранения: " + save.getLastSaveTime());
        } else {
            System.out.println("Данные о сохранении не инициализированы.");
        }
    }
}
