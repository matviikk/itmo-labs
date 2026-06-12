package Commands;

import model.Difficulty;
import model.LabWork;
import utility.*;

import java.util.TreeSet;
/**
 * Команда для удаления из коллекции одного элемента с заданной сложностью.
 */
public class RemoveAnyByDifficulty extends Command {

    private final LabWorkCollectionManager labWorkCollectionManager;
    ScannerManager scannerManager;
    /**
     * Конструктор класса RemoveAnyByDifficulty.
     * @param scannerManager Менеджер сканера для ввода данных.
     */
    public RemoveAnyByDifficulty(ScannerManager scannerManager, LabWorkCollectionManager labWorkCollectionManager) {
        super("remove_any_by_difficulty difficulty", "удалить из коллекции один элемент, значение поля difficulty которого эквивалентно заданному");
        this.scannerManager = scannerManager;
        this.labWorkCollectionManager = labWorkCollectionManager;
    }

    /**
     * Выполняет команду удаления элемента по сложности.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        Difficulty difficulty = Difficulty.valueOf(request.getCommand().split(" ")[1]);
        labWorkCollectionManager.removeByDifficulty(difficulty);
        return new Response("Success");
    }
}
