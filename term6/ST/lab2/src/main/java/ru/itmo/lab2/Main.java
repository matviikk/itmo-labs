package ru.itmo.lab2;

import ru.itmo.lab2.csv.CsvWriter;
import ru.itmo.lab2.functions.Func;
import ru.itmo.lab2.functions.log.LnFunction;
import ru.itmo.lab2.functions.log.LogN;
import ru.itmo.lab2.functions.system.NegativeBranch;
import ru.itmo.lab2.functions.system.PositiveBranch;
import ru.itmo.lab2.functions.system.SystemFunction;
import ru.itmo.lab2.functions.trig.CosFunction;
import ru.itmo.lab2.functions.trig.CotFunction;
import ru.itmo.lab2.functions.trig.CscFunction;
import ru.itmo.lab2.functions.trig.SecFunction;
import ru.itmo.lab2.functions.trig.SinFunction;
import ru.itmo.lab2.functions.trig.TanFunction;

import java.nio.file.Path;
import java.util.Map;

public final class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            System.err.println("Usage: Main <module> <from> <to> <step> <out.csv> [eps=1e-10] [separator=;]");
            System.exit(1);
        }
        String module = args[0];
        double from   = parseDouble(args[1], "from");
        double to     = parseDouble(args[2], "to");
        double step   = parseDouble(args[3], "step");
        Path   out    = Path.of(args[4]);
        double eps    = args.length > 5 ? parseDouble(args[5], "eps") : 1e-10;
        String separator = args.length > 6 ? args[6] : ";";

        Func sin   = new SinFunction(eps);
        Func cos   = new CosFunction(sin);
        Func ln    = new LnFunction(eps);

        Map<String, Func> modules = Map.ofEntries(
            Map.entry("sin",   sin),
            Map.entry("cos",   cos),
            Map.entry("tan",   new TanFunction(sin, cos)),
            Map.entry("cot",   new CotFunction(sin, cos)),
            Map.entry("sec",   new SecFunction(cos)),
            Map.entry("csc",   new CscFunction(sin)),
            Map.entry("ln",    ln),
            Map.entry("log2",  new LogN(ln, 2.0)),
            Map.entry("log3",  new LogN(ln, 3.0)),
            Map.entry("log5",  new LogN(ln, 5.0)),
            Map.entry("log10", new LogN(ln, 10.0)),
            Map.entry("neg",   new NegativeBranch(
                new CscFunction(sin),
                new TanFunction(sin, cos),
                new CotFunction(sin, cos),
                new SecFunction(cos))),
            Map.entry("pos",   new PositiveBranch(
                new LogN(ln, 2.0),
                new LogN(ln, 3.0),
                new LogN(ln, 5.0),
                new LogN(ln, 10.0))),
            Map.entry("system", SystemFunction.withRealModules(eps))
        );

        Func f = modules.get(module);
        if (f == null) {
            System.err.println("Unknown module '" + module + "'. Known: " + modules.keySet());
            System.exit(2);
            return;
        }
        new CsvWriter(separator, "X" + separator + module + "(X)").write(f, from, to, step, out);
        System.out.println("Wrote " + out.toAbsolutePath());
    }

    static double parseDouble(String raw, String argName) {
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException | NullPointerException e) {
            System.err.println("Invalid value for <" + argName + ">: '" + raw + "' is not a number.");
            System.exit(3);
            return Double.NaN;
        }
    }

    private Main() {}
}
