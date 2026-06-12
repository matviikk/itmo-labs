package Commands;

import model.LabWork;
import utility.Request;
import utility.Response;

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
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        clear();
        return new Response("Success");
    }
}
