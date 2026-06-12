package ru.itmo.web.lab3.util;


/**
 * Утилита для проверки попадания точки в заданную область.
 */
public class AreaChecker {

    /**
     * Проверяет, находится ли точка внутри заданной области.
     *
     * @param x координата X точки
     * @param y координата Y точки
     * @param r радиус области
     * @return true, если точка находится внутри области; false в противном случае
     */
    public static boolean checkHit(double x, double y, double r) {

        boolean inQuarterCircle = x >= 0 && y <= 0 && 
            (x * x + y * y) <= (r/2) * (r/2);

        boolean inTriangle = x >= 0 && y >= 0 && y <= r - x;

        boolean inSquare = x <= 0 && y <= 0 && x >= -r && y >= -r;

        return inQuarterCircle || inTriangle || inSquare;
    }
}