package Commands;

import model.LabWork;
import utility.LabWorkCollectionManager;
import utility.Request;
import utility.Response;

import java.util.TreeSet;
/**
 * Команда для вычисления среднего значения средних оценок всех лабораторных работ в коллекции.
 */
public class AverageOfAveragePoint extends Command {

    private final LabWorkCollectionManager labWorkCollectionManager;
    /**
     * Конструктор класса AverageOfAveragePoint.
     */
    public AverageOfAveragePoint(LabWorkCollectionManager labWorkCollectionManager) {
        super("average_of_average_point", "вывести среднее значение поля averagePoint для всех элементов коллекции");
        this.labWorkCollectionManager = labWorkCollectionManager;
    }
    /**
     * Вычисляет среднее значение средних оценок всех лабораторных работ в коллекции.
     */
    public String averageOfAveragePoint() {
        double sum = 0;
        for (LabWork lb: labWorkCollectionManager.getSyncCollection()) {
            sum += lb.getAveragePoint();
        }
        return (sum / labWorkCollectionManager.getSyncCollection().size()) + "";
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
