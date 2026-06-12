package ru.itmo.lab2.functions.system;

import ru.itmo.lab2.functions.Func;

public final class NegativeBranch implements Func {

    private final Func csc;
    private final Func tan;
    private final Func cot;
    private final Func sec;

    public NegativeBranch(Func csc, Func tan, Func cot, Func sec) {
        this.csc = csc;
        this.tan = tan;
        this.cot = cot;
        this.sec = sec;
    }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return Double.NaN;
        double cscX = csc.apply(x);
        double tanX = tan.apply(x);
        double cotX = cot.apply(x);
        double secX = sec.apply(x);
        return (cscX * cscX * cscX * tanX) - (cotX - secX);
    }
}
