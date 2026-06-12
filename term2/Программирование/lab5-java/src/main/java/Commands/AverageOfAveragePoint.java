package Commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import model.LabWork;

import java.util.TreeSet;
/**
 * Команда для вычисления среднего значения средних оценок всех лабораторных работ в коллекции.
 */
public class AverageOfAveragePoint extends Command {
    private final TreeSet<LabWork> treeSet;
    /**
     * Конструктор класса AverageOfAveragePoint.
     * @param treeSet Коллекция лабораторных работ.
     */
    public AverageOfAveragePoint(TreeSet<LabWork> treeSet) {
        super("average_of_average_point", "вывести среднее значение поля averagePoint для всех элементов коллекции");
        this.treeSet = treeSet;
    }
    /**
     * Вычисляет среднее значение средних оценок всех лабораторных работ в коллекции.
     */
    public void averageOfAveragePoint() {
        double sum = 0;
        for (LabWork lb: treeSet) {
            sum += lb.getAveragePoint();
        }
        System.out.println(sum / treeSet.size());
    }
    /**
     * Выполняет команду вычисления среднего значения средних оценок.
     * @param args Аргументы команды (не используются).
     */
    @Override
    public void execute(String... args) {
        averageOfAveragePoint();
    }
}
