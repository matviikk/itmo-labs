package ru.itmo.lab1.domain;

public final class Bombardment {
    private boolean active;
    private int intensity; // 0..100

    public Bombardment(boolean active, int intensity) {
        setActive(active);
        setIntensity(intensity);
        validate();
    }

    public boolean isActive() { return active; }
    public int getIntensity() { return intensity; }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public void validate() {
        if (intensity < 0 || intensity > 100) throw new IllegalArgumentException("intensity must be in [0..100]");
        if (active && intensity == 0) throw new IllegalArgumentException("active bombardment must have intensity > 0");
    }
}