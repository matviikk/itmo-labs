package Commands;

import model.LabWork;
import utility.LabWorkCollectionManager;
import utility.Request;
import utility.Response;

import java.util.TreeSet;
/**
 * Команда для вывода всех элементов коллекции в стандартный поток вывода.
 */
public class Show extends Command {
    private final LabWorkCollectionManager labWorkCollectionManager;
    /**
     * Конструктор класса Show.
     */
    public Show(LabWorkCollectionManager labWorkCollectionManager) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.labWorkCollectionManager = labWorkCollectionManager;
    }
    /**
     * Выполняет команду показа всех элементов коллекции.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        StringBuilder result = new StringBuilder();
        for (LabWork lb: labWorkCollectionManager.getSyncCollection()) {
            result.append(lb);
            result.append("\n");
        }
        return new Response(result.toString());
    }
}
