package Commands;

import model.LabWork;
import utility.Request;
import utility.Response;

import java.util.Date;
import java.util.TreeSet;

/**
 * Команда для вывода информации о коллекции лабораторных работ.
 */
public class Info extends Command {
    private final TreeSet<LabWork> treeSet;
    private final Save save;

    /**
     * Конструктор команды Info.
     *
     * @param treeSet Коллекция лабораторных работ, для которой будет выводиться информация.
     */

    public Info(TreeSet<LabWork> treeSet, Save save) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        this.treeSet = treeSet;
        this.save = save;
    }

    /**
     * Выполняет команду, выводя информацию о коллекции.
     *
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        String result = "Collection type: " + treeSet.getClass().getName();
        result += "Initialization date: " + new Date();
        result += "\n";
        result += "Collection size: " + treeSet.size();
        result += "\n";
        if (save.getLastSaveTime() != null) {
            result += "Дата последнего сохранения: " + save.getLastSaveTime();
        } else {
            result += "Данные о сохранении не инициализированы.";
        }
        return new Response(result);
    }
}
