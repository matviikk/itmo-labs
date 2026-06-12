package ru.itmo.lab2.support;

import ru.itmo.lab2.stubs.TableStub;

import java.util.TreeMap;
import java.util.function.DoubleUnaryOperator;

public final class StubFactory {

    private StubFactory() {}

    public static TableStub stub(String name, DoubleUnaryOperator f, double... xs) {
        TreeMap<Double, Double> data = new TreeMap<>();
        for (double x : xs) data.put(x, f.applyAsDouble(x));
        return new TableStub(name, data);
    }

    public static TableStub sinStub(double... xs) { return stub("sin", Math::sin, xs); }
    public static TableStub cosStub(double... xs) { return stub("cos", Math::cos, xs); }
    public static TableStub tanStub(double... xs) { return stub("tan", Math::tan, xs); }
    public static TableStub cotStub(double... xs) { return stub("cot", x -> Math.cos(x) / Math.sin(x), xs); }
    public static TableStub secStub(double... xs) { return stub("sec", x -> 1.0 / Math.cos(x), xs); }
    public static TableStub cscStub(double... xs) { return stub("csc", x -> 1.0 / Math.sin(x), xs); }

    public static TableStub lnStub(double... xs) { return stub("ln", Math::log, xs); }
    public static TableStub logNStub(double base, double... xs) {
        return stub("log_" + base, x -> Math.log(x) / Math.log(base), xs);
    }
}
