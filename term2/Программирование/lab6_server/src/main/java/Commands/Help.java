package Commands;

import utility.CommandManager;
import utility.Request;
import utility.Response;

import java.util.Map;
/**
 * Команда для отображения справочной информации по доступным командам в приложении.
 */
public class Help extends Command {
    private final CommandManager commandManager;
    public Help(CommandManager commandManager) {
        super("help", "вывести справку по доступным командам");
        this.commandManager = commandManager;
    }
    /**
     * Выводит в стандартный поток вывода список доступных команд с их описанием.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        String res = "Available commands:";
        res += "\n";
        res += "-------------------";
        res += "\n";
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Command> entry : commandManager.getCommands().entrySet()) {
            String output = String.format("%-35s : %s", entry.getValue().getName(), entry.getValue().getDescription());
            result.append(output).append("\n");
        }
        return new Response(res + result);
    }
}
