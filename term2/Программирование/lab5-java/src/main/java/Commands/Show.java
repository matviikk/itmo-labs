package Commands;

import model.LabWork;

import java.util.TreeSet;
/**
 * Команда для вывода всех элементов коллекции в стандартный поток вывода.
 */
public class Show extends Command {
    private final TreeSet<LabWork> treeSet;
    /**
     * Конструктор класса Show.
     * @param treeSet Коллекция лабораторных работ, элементы которой будут отображаться.
     */
    public Show(TreeSet<LabWork> treeSet) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.treeSet = treeSet;
    }
    /**
     * Выводит все элементы коллекции в стандартный поток вывода.
     */
    public void show(){
        for (LabWork lb: treeSet) {
            System.out.println(lb);
        }
    }
    /**
     * Выполняет команду показа всех элементов коллекции.
     * @param args Аргументы команды (не используются в этой команде).
     */
    @Override
    public void execute(String... args) {
        show();
    }
}
