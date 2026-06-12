package Commands;

import model.LabWork;
import utility.Request;
import utility.Response;

import java.util.TreeSet;
/**
 * Команда для вывода всех элементов коллекции в стандартный поток вывода.
 */
public class Show extends Command {
    private final TreeSet<LabWork> treeSet;
    /**
     * Конструктор класса Show.
     * @param treeSet Коллекция лабораторных работ, элементы которой будут отображаться.
     */
    public Show(TreeSet<LabWork> treeSet) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.treeSet = treeSet;
    }
    /**
     * Выполняет команду показа всех элементов коллекции.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        StringBuilder result = new StringBuilder();
        for (LabWork lb: treeSet) {
            result.append(lb);
            result.append("\n");
        }
        return new Response(result.toString());
    }
}
