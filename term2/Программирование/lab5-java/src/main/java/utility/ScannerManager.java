package utility;

import org.example.Main;

import java.util.Scanner;
/**
 * Управляет вводом данных, обеспечивая переключение между стандартным вводом и вводом из файла.
 */
public class ScannerManager {
    private Scanner scanner = new Scanner(System.in); // Сканер для чтения ввода из стандартного ввода
    boolean isReadingFile; // Флаг, указывающий, происходит ли чтение из файла
    private Scanner fileScanner; // Сканер для чтения ввода из файла
    /**
     * Читает следующую строку ввода, выбирая источник в зависимости от текущего режима (файл или стандартный ввод).
     * @return Возвращает следующую строку из соответствующего источника.
     */
    public String nextLine() {
        if (isReadingFile) {
            return fileScanner.nextLine();
        } else {
            return scanner.nextLine();
        }
    }
    /**
     * Проверяет, есть ли следующая строка для чтения в текущем источнике ввода.
     * @return true, если следующая строка доступна, иначе false.
     */
    public boolean hasNext() {
        if (isReadingFile) {
            return fileScanner.hasNext();
        } else {
            return scanner.hasNext();
        }
    }
    /**
     * Проверяет, есть ли следующая строка для чтения.
     * Этот метод использует встроенный метод `hasNextLine` объекта `Scanner`,
     * чтобы определить, может ли сканер прочитать следующую строку из входного потока.
     *
     * @return true, если в потоке ввода доступна следующая строка, иначе false.
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }
    /**
     * Получает текущий Scanner, используемый для ввода.
     * @return текущий объект Scanner.
     */
    public Scanner getScanner() {
        return scanner;
    }
    /**
     * Задает объект Scanner для чтения ввода.
     * @param scanner новый объект Scanner.
     */
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
    /**
     * Проверяет, используется ли в данный момент чтение из файла.
     * @return true, если чтение происходит из файла, иначе false.
     */
    public boolean isReadingFile() {
        return isReadingFile;
    }
    /**
     * Устанавливает флаг чтения из файла.
     * @param readingFile новое состояние флага чтения из файла.
     */
    public void setReadingFile(boolean readingFile) {
        isReadingFile = readingFile;
    }
    /**
     * Получает текущий Scanner, используемый для чтения из файла.
     * @return объект Scanner, используемый для файла.
     */
    public Scanner getFileScanner() {
        return fileScanner;
    }
    /**
     * Задает Scanner для чтения из файла.
     * @param fileScanner новый объект Scanner для чтения из файла.
     */
    public void setFileScanner(Scanner fileScanner) {
        this.fileScanner = fileScanner;
    }
}
