package Commands;

import model.LabWork;

import java.util.Objects;
import java.util.TreeSet;

/**
 * Команда для удаления элемента из коллекции по его идентификатору (ID).
 */
public class RemoveById extends Command {
    private final TreeSet<LabWork> treeSet;
    /**
     * Конструктор класса RemoveById.
     * @param treeSet Коллекция лабораторных работ, из которой будет удален элемент.
     */
    public RemoveById(TreeSet<LabWork> treeSet) {
        super("remove_by_id id", "удалить элемент из коллекции по его id");
        this.treeSet = treeSet;
    }
    /**
     * Удаляет элемент из коллекции по заданному ID.
     * @param id идентификатор лабораторной работы, которую нужно удалить.
     */
    public void removeById(Integer id){
        LabWork temp = null;
        for (LabWork lb: treeSet){
            if (Objects.equals(lb.getId(), id)){
                temp = lb;
            }
        }
        treeSet.remove(temp);
    }
    /**
     * Выполняет команду удаления элемента по ID.
     * @param args Аргументы команды, где args[0] - ID элемента для удаления.
     */
    @Override
    public void execute(String... args) {
        int id = Integer.parseInt(args[0]);
        removeById(id);
    }
}
