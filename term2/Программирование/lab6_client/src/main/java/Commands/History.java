package Commands;

import utility.CommandManager;
import utility.Request;
import utility.Response;

/**
 * Команда для вывода истории последних выполненных команд.
 */
public class History extends Command {
    private final CommandManager commandManager;
    public History(CommandManager commandManager) {
        super("history", "вывести последние 9 команд (без их аргументов)");
        this.commandManager = commandManager;
    }

    /**
     * Выполняет команду, выводя историю последних команд, введённых пользователем.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        return new Response(commandManager.getHistory().toString());
    }
}
