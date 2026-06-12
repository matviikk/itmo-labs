import static java.lang.Math.*;

public class Lab1 {
    public static void main(String[] args) {
        short[] c = new short[7];
        double[] x = new double[13];
        double[][] matrix = new double[7][13];
        c = fillArrayC(c);
        x = fillArrayX(x);
        matrix = fillArrayMatrix(matrix, c, x);
        printArrayThreeDimensional(matrix);
    }

    static short[] fillArrayC(short[] array) { // заполняем массив "c"
        array[0] = 7;
        for (int i = 1; i < array.length; i++) {
            int s = array[i - 1] + 2;
            array[i] = (short) s;
            //System.out.println(c[i]);
        }
        return array;
    }

    static double[] fillArrayX(double[] array) { // заполняем массив "x"
        double max = 7.0d;
        double min = -5.0d;
        for (int i = 0; i < array.length; i++) {
            array[i] = (random() * (max - min) + min);
            //System.out.println(x[i]);
        }
        return array;
    }

    static double[][] fillArrayMatrix(double[][] array, short[] c, double[] x) { // заполняем двумерный массив "matrix"
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (c[i] == 11) {
                    array[i][j] = cbrt(pow(pow(x[j], 2 * x[j]), PI * (pow(x[j], 2 * x[j]) + 2 / 3)));

                }
                if (c[i] == 9 || c[i] == 13 || c[i] == 17) {
                    array[i][j] = pow((4 + sin(toRadians(cbrt(x[j])))) / 2, 3);
                } else {
                    array[i][j] = cbrt(pow(E, pow(((pow(3 * (x[j] + 1), 2) - 1 / 2) / 1) / 2, 3)));
                }
            }
        }
        return array;
    }

    static void printArrayThreeDimensional(double[][] array) { // вывод матрицы
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.printf("%10.3f   ", array[i][j]);
            }
            System.out.printf("\n");
        }
    }
}