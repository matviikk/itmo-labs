package ru.itmo.lab2.support;

public final class Reference {

    private Reference() {}

    public static double negativeBranch(double x) {
        double sinX = Math.sin(x);
        double cosX = Math.cos(x);
        double csc  = 1.0 / sinX;
        double tan  = sinX / cosX;
        double cot  = cosX / sinX;
        double sec  = 1.0 / cosX;
        return (csc * csc * csc * tan) - (cot - sec);
    }

    public static double positiveBranch(double x) {
        double l2  = Math.log(x) / Math.log(2.0);
        double l3  = Math.log(x) / Math.log(3.0);
        double l5  = Math.log(x) / Math.log(5.0);
        double l10 = Math.log10(x);
        double numerator   = ((l2 - l5) / l10 + l5) - (l10 * l5);
        double denominator = (l10 / l3) + l5;
        return numerator / denominator;
    }

    public static double system(double x) {
        return (x <= 0.0) ? negativeBranch(x) : positiveBranch(x);
    }
}
