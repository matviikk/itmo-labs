package utility;

import Commands.*;
import model.LabWork;

import java.util.*;

/**
 * Менеджер команд, управляющий выполнением всех доступных команд.
 * Хранит множество лабораторных работ и обеспечивает управление ими через консоль.
 */
public class CommandManager {
    private final Map<String, Command> map = new HashMap<String, Command>();

    /**
     * История введенных команд.
     */
    private final Deque<String> history = new ArrayDeque<>();
    /**
     * Конструктор инициализирует CommandManager с заданным набором работ, сканером и путем для сохранения.
     *
     * @param treeSet Набор лабораторных работ.
     * @param scannerManager Менеджер для обработки пользовательского ввода.
     * @param path Путь для сохранения данных.
     */
    public CommandManager(TreeSet<LabWork> treeSet, ScannerManager scannerManager, String path) {
        // инициализация команд с их соответствующими обработчиками
        Save saveInstance = new Save(treeSet, path);
        map.put("help", new Help(this));
        map.put("info", new Info(treeSet, saveInstance));
        map.put("show", new Show(treeSet));
        map.put("add", new Add(scannerManager, treeSet));
        map.put("update", new Update(scannerManager, treeSet));
        map.put("remove_by_id", new RemoveById(treeSet));
        map.put("clear", new Clear(treeSet));
        map.put("save", saveInstance);
        //map.put("exit", new Exit(runner));
        map.put("remove_greater", new RemoveGreater(scannerManager, treeSet));
        map.put("remove_lower", new RemoveLower(scannerManager, treeSet));
        map.put("history", new History(this));
        map.put("remove_any_by_difficulty", new RemoveAnyByDifficulty(scannerManager, treeSet));
        map.put("average_of_average_point", new AverageOfAveragePoint(treeSet));
        map.put("print_descending", new PrintDescending(treeSet));
        map.put("execute_script", new ExecuteScript(scannerManager, this));
    }
    public static Set<String> commandsWithLabWorkAsArgument = Set.of("add", "update", "remove_greater", "remove_lower");
    /**
     * Выполняет команду, указанную пользователем.
     *
     * @param
     * @return
     */
    public Response executeCommand(Request request) {

        String[] commandArray = request.getCommand().split(" ");
        String commandName = commandArray[0];
        if (!map.containsKey(commandName)) {
            System.out.println("Такой команды не существует.");
            return new Response(null, "Такой команды не существует.");
        }
        try {
            history.addFirst(commandName);
            if (history.size() > 9) {
                history.removeLast();
            }
            return map.get(commandName).execute(request);
        } catch (NoSuchElementException e) {
            return new Response(null, "Нет входных данных для команды :(");
        } catch (Exception e) {
            return new Response(null, "Произошла ошибка при выполнении команды...");
        }
    }
    /**
     * Возвращает карту всех доступных команд.
     *
     * @return Карта команд.
     */
    public Map<String, Command> getCommands() {
        return map;
    }


    public Deque<String> getHistory() {
        return history;
    }
}
