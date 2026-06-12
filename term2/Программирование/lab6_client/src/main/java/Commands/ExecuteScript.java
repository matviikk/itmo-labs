package Commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import utility.CommandManager;
import utility.Request;
import utility.Response;
import utility.ScannerManager;
/**
 * Команда для выполнения набора команд из файла скрипта.
 */
public class ExecuteScript extends Command {
    private final ScannerManager scannerManager;
    private final CommandManager commandManager;
    private final Set<String> memory = new HashSet<>();
    /**
     * Конструктор класса ExecuteScript.
     * @param scannerManager Экземпляр ScannerManager для управления чтением данных.
     */
    public ExecuteScript(ScannerManager scannerManager, CommandManager commandManager) {
        super("execute_script file_name", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме");
        this.scannerManager = scannerManager;
        this.commandManager = commandManager;
    }
    /**
     * Выполняет команды, перечисленные в файле скрипта.
     * @param request
     * Предотвращает бесконечную рекурсию и выкидывает RuntimeException, если она найдена.
     * @return
     */
    @Override
    public Response execute(Request request) {
        String[] args = request.getCommand().split(" ");
        String filepath = args[1];
        Scanner currentScanner;
        try {
            currentScanner = new Scanner(new File(filepath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        scannerManager.setFileScanner(currentScanner);
        memory.add(filepath);
        scannerManager.setReadingFile(true);
        StringBuilder result = new StringBuilder();
        while (scannerManager.hasNext()) {
            String s = scannerManager.nextLine();
            if (s.split(" ")[0].equals("execute_script")) {
                if (memory.contains(s.split(" ")[1])) {
                    throw new RuntimeException("Найдена бесконечная рекурсия");
                }
            }
            Request request1 = new Request(s, null);
            result.append(commandManager.executeCommand(request1).getMessage());
            result.append("\n");
            scannerManager.setFileScanner(currentScanner);
            scannerManager.setReadingFile(true);
        }
        scannerManager.setReadingFile(false);
        return new Response(result.toString());
    }
}
