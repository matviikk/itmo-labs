package ru.itmo.web.lab4.util;

public class AreaChecker {

    public static boolean checkHit(double x, double y, double r) {

        boolean inQuarterCircle = x >= 0 && y <= 0 && 
            (x * x + y * y) <= (r/2) * (r/2);

        boolean inTriangle = x >= 0 && y >= 0 && y <= r - x;

        boolean inSquare = x <= 0 && y <= 0 && x >= -r && y >= -r;

        return inQuarterCircle || inTriangle || inSquare;
    }
}