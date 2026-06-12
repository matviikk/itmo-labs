package ru.itmo.lab1.domain;

public final class Environment {
    private int heatLevel;  // 0..100
    private int noiseLevel; // 0..100

    public Environment(int heatLevel, int noiseLevel) {
        setHeatLevel(heatLevel);
        setNoiseLevel(noiseLevel);
    }

    public int getHeatLevel() { return heatLevel; }
    public int getNoiseLevel() { return noiseLevel; }

    public void setHeatLevel(int heatLevel) {
        if (heatLevel < 0 || heatLevel > 100) throw new IllegalArgumentException("heatLevel must be in [0..100]");
        this.heatLevel = heatLevel;
    }

    public void setNoiseLevel(int noiseLevel) {
        if (noiseLevel < 0 || noiseLevel > 100) throw new IllegalArgumentException("noiseLevel must be in [0..100]");
        this.noiseLevel = noiseLevel;
    }

    public void increaseHeat(int delta) {
        setHeatLevel(Math.min(100, heatLevel + Math.max(0, delta)));
    }

    public void increaseNoise(int delta) {
        setNoiseLevel(Math.min(100, noiseLevel + Math.max(0, delta)));
    }
}