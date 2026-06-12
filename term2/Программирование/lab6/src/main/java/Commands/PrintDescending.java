package Commands;

import model.LabWork;
import utility.LabWorkCollectionManager;
import utility.Request;
import utility.Response;

import java.util.Iterator;
import java.util.TreeSet;
/**
 * Команда для вывода элементов коллекции в порядке убывания.
 */
public class PrintDescending extends Command {

    private final LabWorkCollectionManager labWorkCollectionManager;
    /**
     * Конструктор класса PrintDescending.
     */
    public PrintDescending(LabWorkCollectionManager labWorkCollectionManager) {
        super("print_descending", "вывести элементы коллекции в порядке убывания");
        this.labWorkCollectionManager = labWorkCollectionManager;
    }
    /**
     * Выводит элементы коллекции в порядке убывания.
     */
    public String printDescending() {
        Iterator<LabWork> iterator = labWorkCollectionManager.getSyncCollection().iterator();
        return recursion(iterator);
    }
    /**
     * Рекурсивно выводит элементы коллекции начиная с последнего до первого.
     * @param iterator итератор для коллекции лабораторных работ.
     */
    public String recursion(Iterator<LabWork> iterator) {
        String result = "";
        if (iterator.hasNext()) {
            LabWork lb = iterator.next();
            result += recursion(iterator);
            result+="\n";
            result += lb.toString();

        }
        return result.trim();
    }
    /**
     * Выполняет команду вывода всех элементов коллекции в порядке убывания.
     * @param args Аргументы команды (не используются в этой команде).
     * @return
     */
    @Override
    public Response execute(Request request) {
        return new Response(printDescending());
    }
}
