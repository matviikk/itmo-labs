package Commands;

import model.LabWork;
import utility.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Команда для удаления из коллекции всех элементов, которые меньше заданного элемента.
 */
public class RemoveLower extends Command {

    private final LabWorkCollectionManager labWorkCollectionManager;
    private final ScannerManager scannerManager;
    /**
     * Конструктор класса RemoveLower.
     * @param scannerManager Менеджер сканера, предоставляющий методы для чтения ввода пользователя.
     */
    public RemoveLower(ScannerManager scannerManager, LabWorkCollectionManager labWorkCollectionManager) {
        super("remove_lower {element}", "удалить из коллекции все элементы, меньшие, чем заданный");
        this.labWorkCollectionManager = labWorkCollectionManager;
        this.scannerManager = scannerManager;
    }

    /**
     * Выполняет команду удаления всех элементов коллекции, меньших заданного элемента.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        LabWork labWork = request.getLabWork();
        labWorkCollectionManager.removeLower(labWork);
        return new Response("Success");
    }
}
