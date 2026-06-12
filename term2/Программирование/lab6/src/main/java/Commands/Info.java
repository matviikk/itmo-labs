package Commands;

import model.LabWork;
import utility.LabWorkCollectionManager;
import utility.Request;
import utility.Response;

import java.util.Date;
import java.util.TreeSet;

/**
 * Команда для вывода информации о коллекции лабораторных работ.
 */
public class Info extends Command {

    private final LabWorkCollectionManager labWorkCollectionManager;

    /**
     * Конструктор команды Info.
     *
     */

    public Info(LabWorkCollectionManager labWorkCollectionManager) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        this.labWorkCollectionManager = labWorkCollectionManager;
    }

    /**
     * Выполняет команду, выводя информацию о коллекции.
     *
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        String result = "Collection type: " + labWorkCollectionManager.getSyncCollection().getClass().getName();
        result += "Initialization date: " + new Date();
        result += "\n";
        result += "Collection size: " + labWorkCollectionManager.getSyncCollection().size();
        result += "\n";
        if (labWorkCollectionManager.getLastSaveTime() != null) {
            result += "Дата последнего сохранения: " + labWorkCollectionManager.getLastSaveTime();
        } else {
            result += "Данные о сохранении не инициализированы.";
        }
        return new Response(result);
    }
}
