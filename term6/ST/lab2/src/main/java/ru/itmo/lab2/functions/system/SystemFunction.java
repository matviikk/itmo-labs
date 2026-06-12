package ru.itmo.lab2.functions.system;

import ru.itmo.lab2.functions.Func;
import ru.itmo.lab2.functions.log.LnFunction;
import ru.itmo.lab2.functions.log.LogN;
import ru.itmo.lab2.functions.trig.CosFunction;
import ru.itmo.lab2.functions.trig.CotFunction;
import ru.itmo.lab2.functions.trig.CscFunction;
import ru.itmo.lab2.functions.trig.SecFunction;
import ru.itmo.lab2.functions.trig.SinFunction;
import ru.itmo.lab2.functions.trig.TanFunction;

public final class SystemFunction implements Func {

    private final Func negative;
    private final Func positive;

    public SystemFunction(Func negative, Func positive) {
        this.negative = negative;
        this.positive = positive;
    }

    public static SystemFunction withRealModules(double eps) {
        Func sin   = new SinFunction(eps);
        Func cos   = new CosFunction(sin);
        Func tan   = new TanFunction(sin, cos);
        Func cot   = new CotFunction(sin, cos);
        Func sec   = new SecFunction(cos);
        Func csc   = new CscFunction(sin);

        Func ln    = new LnFunction(eps);
        Func log2  = new LogN(ln, 2.0);
        Func log3  = new LogN(ln, 3.0);
        Func log5  = new LogN(ln, 5.0);
        Func log10 = new LogN(ln, 10.0);

        return new SystemFunction(
            new NegativeBranch(csc, tan, cot, sec),
            new PositiveBranch(log2, log3, log5, log10)
        );
    }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x)) return Double.NaN;
        return (x <= 0.0) ? negative.apply(x) : positive.apply(x);
    }
}
