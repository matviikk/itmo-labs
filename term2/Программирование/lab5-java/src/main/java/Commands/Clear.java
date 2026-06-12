package Commands;

import model.LabWork;
import java.util.TreeSet;
/**
 * Команда для очистки всех лабораторных работ из коллекции.
 */
public class Clear extends Command {
    private final TreeSet<LabWork> treeSet;
    /**
     * Конструктор класса Clear.
     * @param treeSet Коллекция лабораторных работ, которая будет очищена.
     */
    public Clear(TreeSet<LabWork> treeSet) {
        super("clear", "очистить коллекцию");
        this.treeSet = treeSet;
    }
    /**
     * Очищает коллекцию лабораторных работ.
     */
    public void clear(){
        treeSet.clear();
    }
    /**
     * Выполняет команду очистки коллекции лабораторных работ.
     * @param args Аргументы команды (не используются).
     */
    @Override
    public void execute(String... args) {
        clear();
    }
}
