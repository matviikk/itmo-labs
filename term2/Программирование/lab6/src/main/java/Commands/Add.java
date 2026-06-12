package Commands;

import model.LabWork;

import java.util.TreeSet;

import utility.*;

/**
 * Команда для добавления новой лабораторной работы в коллекцию.
 */
public class Add extends Command {
    private final LabWorkCollectionManager labWorkCollectionManager;
    private final ScannerManager scannerManager; // Менеджер для обработки ввода пользователя
    /**
     * Конструктор класса Add.
     * @param scannerManager Менеджер сканера, предоставляющий методы для чтения ввода пользователя.
     */
    public Add(ScannerManager scannerManager, LabWorkCollectionManager labWorkCollectionManager) {
        super("add {element}", "добавить новый элемент в коллекцию");
        this.labWorkCollectionManager = labWorkCollectionManager;
        this.scannerManager = scannerManager;
    }

    /**
     * Выполняет команду добавления лабораторной работы, парся данные ввода пользователя.
     * @param request Аргументы команды (не используются).
     * @return
     */
    @Override
    public Response execute(Request request) {
        int id = labWorkCollectionManager.add(request.getLabWork(), labWorkCollectionManager.getUserByUsername(request.getLogin()));
        return new Response("Success, generated ID: " + id);
    }
}
