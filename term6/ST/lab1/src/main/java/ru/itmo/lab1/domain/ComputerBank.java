package ru.itmo.lab1.domain;

public final class ComputerBank {
    private int integrity;        // 0..100
    private int frontMeltPercent; // 0..100

    public ComputerBank(int integrity, int frontMeltPercent) {
        setIntegrity(integrity);
        setFrontMeltPercent(frontMeltPercent);
    }

    public int getIntegrity() { return integrity; }
    public int getFrontMeltPercent() { return frontMeltPercent; }

    public void setIntegrity(int integrity) {
        if (integrity < 0 || integrity > 100) throw new IllegalArgumentException("integrity must be in [0..100]");
        this.integrity = integrity;
    }

    public void setFrontMeltPercent(int frontMeltPercent) {
        if (frontMeltPercent < 0 || frontMeltPercent > 100) throw new IllegalArgumentException("frontMeltPercent must be in [0..100]");
        this.frontMeltPercent = frontMeltPercent;
    }

    public void damage(int delta) {
        int d = Math.max(0, delta);
        setIntegrity(Math.max(0, integrity - d));
    }

    public void meltFront(int delta) {
        int d = Math.max(0, delta);
        setFrontMeltPercent(Math.min(100, frontMeltPercent + d));
    }

    public boolean isFrontMostlyMelted() {
        return frontMeltPercent >= 90;
    }
}