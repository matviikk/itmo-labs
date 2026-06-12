package ru.itmo.lab1.domain;

public final class MoltenMetalFlow {
    private boolean active;
    private Corner direction;
    private int thickness; // 1..10 when active, else 0

    public MoltenMetalFlow(boolean active, Corner direction, int thickness) {
        this.active = active;
        this.direction = direction == null ? Corner.NONE : direction;
        this.thickness = thickness;
        validate();
    }

    public boolean isActive() { return active; }
    public Corner getDirection() { return direction; }
    public int getThickness() { return thickness; }

    public void activateToCorner(int thickness) {
        this.active = true;
        this.direction = Corner.TARGET_CORNER;
        this.thickness = thickness;
        validate();
    }

    public void deactivate() {
        this.active = false;
        this.direction = Corner.NONE;
        this.thickness = 0;
    }

    public void validate() {
        if (!active) {
            if (direction != Corner.NONE) throw new IllegalArgumentException("inactive flow must have direction NONE");
            if (thickness != 0) throw new IllegalArgumentException("inactive flow must have thickness 0");
            return;
        }
        if (direction != Corner.TARGET_CORNER) throw new IllegalArgumentException("active flow must target corner");
        if (thickness < 1 || thickness > 10) throw new IllegalArgumentException("thickness must be in [1..10]");
    }
}