package ru.itmo.lab1;
public final class TanSeries {

    public static final double DEFAULT_EPS = 1e-12;
    public static final int DEFAULT_MAX_TERMS = 200;

    private TanSeries() {}

    public static double tan(double x) {
        return tan(x, DEFAULT_EPS, DEFAULT_MAX_TERMS);
    }

    public static double tan(double x, double eps, int maxTerms) {
        validateInputs(x, eps, maxTerms);

        double xr = reduceToMinusHalfPiToHalfPi(x);

        double sin = sinSeries(xr, eps, maxTerms);
        double cos = cosSeries(xr, eps, maxTerms);

        if (Math.abs(cos) < eps) {
            throw new ArithmeticException("tan(x) is undefined or unstable near pi/2 + k*pi: cos(x) ~ 0");
        }
        return sin / cos;
    }

    static void validateInputs(double x, double eps, int maxTerms) {
        if (!Double.isFinite(x)) throw new IllegalArgumentException("x must be finite");
        if (!(eps > 0.0) || Double.isNaN(eps)) throw new IllegalArgumentException("eps must be > 0");
        if (maxTerms <= 0) throw new IllegalArgumentException("maxTerms must be > 0");
    }

    static double reduceToMinusHalfPiToHalfPi(double x) {
        double r = Math.IEEEremainder(x, 2.0 * Math.PI);

        if (r > Math.PI / 2.0) {
            r -= Math.PI;
        } else if (r < -Math.PI / 2.0) {
            r += Math.PI;
        }
        return r;
    }

    static double sinSeries(double x, double eps, int maxTerms) {
        double term = x;
        double sum = term;

        for (int n = 1; n < maxTerms; n++) {
            double denom = (2.0 * n) * (2.0 * n + 1.0);
            term *= -x * x / denom;
            sum += term;
            if (Math.abs(term) < eps) break;
        }
        return sum;
    }

    static double cosSeries(double x, double eps, int maxTerms) {
        double term = 1.0;
        double sum = term;

        for (int n = 1; n < maxTerms; n++) {
            double denom = (2.0 * n - 1.0) * (2.0 * n);
            term *= -x * x / denom;
            sum += term;
            if (Math.abs(term) < eps) break;
        }
        return sum;
    }
}