package ru.itmo.lab2.csv;

import ru.itmo.lab2.functions.Func;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class CsvWriter {

    private final String separator;
    private final String header;

    public CsvWriter(String separator, String header) {
        this.separator = separator;
        this.header = header;
    }

    public void write(Func func, double from, double to, double step, Path out) throws IOException {
        if (!(step > 0.0) || !Double.isFinite(step)) {
            throw new IllegalArgumentException("step must be positive and finite");
        }
        if (from > to) {
            throw new IllegalArgumentException("from must be <= to");
        }
        Files.createDirectories(out.getParent() == null ? Path.of(".") : out.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            writeTo(func, from, to, step, bw);
        }
    }

    public void writeTo(Func func, double from, double to, double step, Writer writer) throws IOException {
        if (header != null && !header.isEmpty()) {
            writer.write(header);
            writer.write('\n');
        }
        int steps = (int) Math.floor((to - from) / step + 1e-9);
        for (int i = 0; i <= steps; i++) {
            double x = from + i * step;
            String yStr;
            try {
                double y = func.apply(x);
                yStr = Double.isNaN(y) ? "NaN" : String.format(Locale.US, "%.12g", y);
            } catch (ArithmeticException e) {
                yStr = "undefined";
            }
            writer.write(String.format(Locale.US, "%.12g", x));
            writer.write(separator);
            writer.write(yStr);
            writer.write('\n');
        }
    }
}
