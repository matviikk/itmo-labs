package Commands;

import model.LabWork;
import utility.LabWorkCollectionManager;
import utility.Request;
import utility.Response;

import java.util.TreeSet;
/**
 * Команда для очистки всех лабораторных работ из коллекции.
 */
public class Clear extends Command {

    private final LabWorkCollectionManager labWorkCollectionManager;
    /**
     * Конструктор класса Clear.
     */
    public Clear(LabWorkCollectionManager labWorkCollectionManager) {
        super("clear", "очистить коллекцию");
        this.labWorkCollectionManager = labWorkCollectionManager;
    }
    /**
     * Выполняет команду очистки коллекции лабораторных работ.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        labWorkCollectionManager.clear(labWorkCollectionManager.getUserByUsername(request.getLogin()));
        return new Response("Success");
    }
}
