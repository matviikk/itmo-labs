package ru.itmo.lab2.functions.trig;

import ru.itmo.lab2.functions.Func;

public final class CosFunction implements Func {

    private final Func sin;

    public CosFunction(Func sin) {
        this.sin = sin;
    }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            return Double.NaN;
        }
        return sin.apply(Math.PI / 2.0 - x);
    }
}
