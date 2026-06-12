public class Checker {

    static boolean validate(double x, double y, double r) {
        return (x >= -5 && x <= 3) && (y > -3 && y < 5) && (r >= 1 && r <= 5);
    }

    private static boolean isTriangle(double x, double y, double r) {
        return x >= 0 && y >= 0 && y <= (r - x);
    }

    private static boolean isCircle(double x, double y, double r) {
        return x >= 0 && y <= 0 && (x * x + y * y <= Math.pow(r, 2));
    }


    private static boolean isRectangle(double x, double y, double r) {
        return x <= 0 && y <= 0 && x >= -r && y >= -r / 2;
    }


    public static boolean isInTheSpot(double x, double y, double r) {
        return isCircle(x, y, r) || isTriangle(x, y, r) || isRectangle(x, y, r);
    }
}
