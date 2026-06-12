package Commands;

import model.LabWork;
import utility.Request;
import utility.Response;

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
    public String averageOfAveragePoint() {
        double sum = 0;
        for (LabWork lb: treeSet) {
            sum += lb.getAveragePoint();
        }
        return (sum / treeSet.size()) + "";
    }
    /**
     * Выполняет команду вычисления среднего значения средних оценок.
     * @param request Аргументы команды (не используются).
     * @return
     */
    @Override
    public Response execute(Request request) {
        return new Response(averageOfAveragePoint());
    }
}
