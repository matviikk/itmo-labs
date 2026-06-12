package Commands;

import model.LabWork;
import utility.LabWorkCollectionManager;
import utility.Request;
import utility.Response;

import java.util.Objects;
import java.util.TreeSet;

/**
 * Команда для удаления элемента из коллекции по его идентификатору (ID).
 */
public class RemoveById extends Command {

    private final LabWorkCollectionManager labWorkCollectionManager;
    /**
     * Конструктор класса RemoveById.
     */
    public RemoveById(LabWorkCollectionManager labWorkCollectionManager) {
        super("remove_by_id id", "удалить элемент из коллекции по его id");
        this.labWorkCollectionManager = labWorkCollectionManager;
    }
    /**
     * Выполняет команду удаления элемента по ID.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        String[] args = request.getCommand().split(" ");
        int id = Integer.parseInt(args[1]);
        labWorkCollectionManager.removeById(id);
        return new Response("Success");
    }
}
