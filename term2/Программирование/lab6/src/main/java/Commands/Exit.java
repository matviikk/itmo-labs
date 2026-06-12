package Commands;

import utility.Request;
import utility.Response;
import utility.Runner;

/**
 * Команда для завершения выполнения программы.
 */
public class Exit extends Command {
    private final Runner runner;
    public Exit(Runner runner) {
        super("exit", "завершить программу (без сохранения в файл");
        this.runner = runner;
    }

    /**
     * Выполняет операцию выхода из программы, устанавливая флаг isRunning в состояние false.
     * @param
     * @return
     */
    @Override
    public Response execute(Request request) {
        //runner.setRunning(false);
        return null;
    }
}
