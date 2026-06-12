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
    private Runner runner;
    /**
     * Конструктор инициализирует CommandManager с заданным набором работ, сканером и путем для сохранения.
     *
     * @param treeSet Набор лабораторных работ.
     * @param scannerManager Менеджер для обработки пользовательского ввода.
     * @param path Путь для сохранения данных.
     */
    public CommandManager(TreeSet<LabWork> treeSet, ScannerManager scannerManager, String path, Runner runner) {
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
        map.put("exit", new Exit(runner));
        map.put("remove_greater", new RemoveGreater(scannerManager, treeSet));
        map.put("remove_lower", new RemoveLower(scannerManager, treeSet));
        map.put("history", new History(runner));
        map.put("remove_any_by_difficulty", new RemoveAnyByDifficulty(scannerManager, treeSet));
        map.put("average_of_average_point", new AverageOfAveragePoint(treeSet));
        map.put("print_descending", new PrintDescending(treeSet));
        map.put("execute_script", new ExecuteScript(scannerManager, this));
    }
    /**
     * Выполняет команду, указанную пользователем.
     *
     * @param string Строка, содержащая команду и её аргументы.
     */
    public void executeCommand(String string) {
        String[] commandArray = string.split(" ");
        String commandName = commandArray[0];
        String[] args = new String[commandArray.length - 1];
        System.arraycopy(commandArray, 1, args, 0, args.length);
        if (!map.containsKey(commandName)) {
            System.out.println("Такой команды не существует.");
            return;
        }
        try {
            map.get(commandName).execute(args);
        } catch (NoSuchElementException e) {
            System.out.println("Нет входных данных для команды :(");
        } catch (Exception e) {
            System.out.println("Произошла ошибка при выполнении команды...");
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
}
