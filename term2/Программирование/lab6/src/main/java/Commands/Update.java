package Commands;

import model.LabWork;
import utility.*;

import java.util.Objects;
import java.util.TreeSet;

/**
 * Команда для обновления элемента коллекции по его ID.
 */
public class Update extends Command {
    private final ScannerManager scannerManager;
    private final LabWorkCollectionManager labWorkCollectionManager;
    /**
     * Конструктор класса Update.
     * @param scannerManager Менеджер сканера, предоставляющий методы для чтения ввода пользователя.
     */
    public Update(ScannerManager scannerManager, LabWorkCollectionManager labWorkCollectionManager) {
        super("update id {element}", "обновить значение элемента коллекции, id которого равен заданному");
        this.scannerManager = scannerManager;
        this.labWorkCollectionManager = labWorkCollectionManager;
    }

    /**
     * Выполняет команду обновления элемента по ID.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        String[] args = request.getCommand().split(" ");

        int id = Integer.parseInt(args[1]);
        System.out.println("UPDATED ID: " + id);
        LabWork element = request.getLabWork();
        element.setId(id);
        labWorkCollectionManager.update(element, labWorkCollectionManager.getUserByUsername(request.getLogin()));
        return new Response("Success");
    }
}
