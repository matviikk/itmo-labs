package ru.itmo.lab1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListTracer implements BTreeTracer {
    private final List<TracePoint> points = new ArrayList<>();

    @Override
    public void hit(TracePoint p) {
        points.add(p);
    }

    public List<TracePoint> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(points));
    }

    public void clear() {
        points.clear();
    }
}
