package Commands;

import model.LabWork;
import utility.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Команда для удаления из коллекции всех элементов, которые превышают заданный элемент.
 */
public class RemoveGreater extends Command {

    private final LabWorkCollectionManager labWorkCollectionManager;
    private final ScannerManager scannerManager;
    /**
     * Конструктор класса RemoveGreater.
     * @param scannerManager Менеджер сканера, предоставляющий методы для чтения ввода пользователя.
     */
    public RemoveGreater(ScannerManager scannerManager, LabWorkCollectionManager labWorkCollectionManager) {
        super("remove_greater {element}", "удалить из коллекции все элементы, превышающие заданный");
        this.labWorkCollectionManager = labWorkCollectionManager;
        this.scannerManager = scannerManager;
    }

    /**
     * Выполняет команду удаления всех элементов коллекции, превышающих заданный элемент.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        LabWork labWork = request.getLabWork();
        labWorkCollectionManager.removeGreater(labWork);
        return new Response("Success");
    }
}
