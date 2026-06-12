package Commands;

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
     * @param args Аргументы команды (не используются в этой команде).
     */
    @Override
    public void execute(String... args) {
        runner.setRunning(false);
    }
}
