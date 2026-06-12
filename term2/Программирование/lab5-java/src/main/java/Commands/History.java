package Commands;

import utility.Runner;
/**
 * Команда для вывода истории последних выполненных команд.
 */
public class History extends Command {
    private final Runner runner;
    public History(Runner runner) {
        super("history", "вывести последние 9 команд (без их аргументов)");
        this.runner = runner;
    }

    /**
     * Выполняет команду, выводя историю последних команд, введённых пользователем.
     * @param args Аргументы команды (не используются в этой команде).
     */
    @Override
    public void execute(String... args) {
        System.out.println(runner.getHistory());
    }
}
