package ru.itmo.lab1;

import java.util.Arrays;

public final class CompositeKey implements Comparable<CompositeKey> {
    private final int[] parts;

    public CompositeKey(int... parts) {
        if (parts == null) throw new IllegalArgumentException("parts is null");
        if (parts.length < 1 || parts.length > 5) {
            throw new IllegalArgumentException("CompositeKey length must be 1..5");
        }
        this.parts = Arrays.copyOf(parts, parts.length);
    }

    public int size() {
        return parts.length;
    }

    public int part(int i) {
        return parts[i];
    }

    @Override
    public int compareTo(CompositeKey o) {
        int n = Math.min(this.parts.length, o.parts.length);
        for (int i = 0; i < n; i++) {
            int cmp = Integer.compare(this.parts[i], o.parts[i]);
            if (cmp != 0) return cmp;
        }
        return Integer.compare(this.parts.length, o.parts.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CompositeKey other)) return false;
        return Arrays.equals(this.parts, other.parts);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(parts);
    }

    @Override
    public String toString() {
        return "K" + Arrays.toString(parts);
    }
}