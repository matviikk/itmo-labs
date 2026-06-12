package ru.itmo.lab2.stubs;

import ru.itmo.lab2.functions.Func;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class TableStub implements Func {

    private final NavigableMap<Double, Double> table;
    private final double matchTol;
    private final String name;

    public TableStub(String name, Map<Double, Double> data) {
        this(name, data, 1e-9);
    }

    public TableStub(String name, Map<Double, Double> data, double matchTol) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Table stub requires at least one data point");
        }
        this.name = name;
        this.table = new TreeMap<>(data);
        this.matchTol = matchTol;
    }

    public String getName() { return name; }

    public NavigableMap<Double, Double> getTable() { return table; }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x)) return Double.NaN;

        Map.Entry<Double, Double> floor = table.floorEntry(x);
        Map.Entry<Double, Double> ceil  = table.ceilingEntry(x);
        if (floor != null && Math.abs(floor.getKey() - x) <= matchTol) return floor.getValue();
        if (ceil  != null && Math.abs(ceil.getKey()  - x) <= matchTol) return ceil.getValue();

        if (floor != null && ceil != null && !floor.getKey().equals(ceil.getKey())) {
            double x0 = floor.getKey(), y0 = floor.getValue();
            double x1 = ceil.getKey(),  y1 = ceil.getValue();
            double t = (x - x0) / (x1 - x0);
            return y0 + t * (y1 - y0);
        }
        throw new IllegalArgumentException(
            "TableStub[" + name + "] has no value for x = " + x +
            " (table covers [" + table.firstKey() + ", " + table.lastKey() + "])");
    }

    public static TableStub fromCsv(String name, Path file, String separator) throws IOException {
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return parse(name, reader, separator);
        }
    }

    public static TableStub fromResource(String name, String resource, String separator) throws IOException {
        InputStream in = TableStub.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            throw new IOException("Stub resource not found on classpath: " + resource);
        }
        try (var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return parse(name, reader, separator);
        }
    }

    private static TableStub parse(String name, BufferedReader reader, String separator) throws IOException {
        NavigableMap<Double, Double> map = new TreeMap<>();
        String line;
        int lineNo = 0;
        while ((line = reader.readLine()) != null) {
            lineNo++;
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            String[] parts = trimmed.split(java.util.regex.Pattern.quote(separator));
            if (parts.length < 2) {
                throw new IOException("Malformed stub line " + lineNo + ": " + line);
            }
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            map.put(x, y);
        }
        return new TableStub(name, map);
    }
}
