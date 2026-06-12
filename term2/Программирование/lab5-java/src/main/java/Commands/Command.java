package Commands;
/**
 * {@code Command} предоставляет абстрактную основу для всех команд в системе,
 * инкапсулируя общие свойства такие как имя и описание команды. Классы, наследующие
 * {@code Command}, должны реализовать метод {@link #execute(String...)} для
 * выполнения конкретных действий команды.
 */
public abstract class Command {
    private String name;
    private String description;
    /**
     * Конструктор для создания команды с указанным именем и описанием.
     *
     * @param name Имя команды. Не может быть {@code null}.
     * @param description Описание команды, объясняющее её функциональность. Не может быть {@code null}.
     */
    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }
    /**
     * Выполняет команду с использованием указанных аргументов.
     *
     * @param args Аргументы, необходимые для выполнения команды.
     */
    public abstract void execute(String... args);
    /**
     * Возвращает имя команды.
     *
     * @return Имя команды.
     */
    public String getName() {
        return name;
    }
    /**
     * Устанавливает новое имя команды.
     *
     * @param name Новое имя команды.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    public String getDescription() {
        return description;
    }
    /**
     * Устанавливает новое описание команды.
     *
     * @param description Новое описание команды.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
