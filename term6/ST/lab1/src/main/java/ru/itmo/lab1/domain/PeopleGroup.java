package ru.itmo.lab1.domain;

public final class PeopleGroup {
    private final int count; // >0
    private Corner position;
    private int cohesion; // 0..100
    private GroupState state;

    public PeopleGroup(int count, Corner position, int cohesion, GroupState state) {
        if (count <= 0) throw new IllegalArgumentException("count must be > 0");
        this.count = count;
        this.position = position == null ? Corner.NONE : position;
        setCohesion(cohesion);
        this.state = state == null ? GroupState.SITTING : state;
        validate();
    }

    public int getCount() { return count; }
    public Corner getPosition() { return position; }
    public int getCohesion() { return cohesion; }
    public GroupState getState() { return state; }

    public void setPosition(Corner position) {
        this.position = position == null ? Corner.NONE : position;
    }

    public void setCohesion(int cohesion) {
        if (cohesion < 0 || cohesion > 100) throw new IllegalArgumentException("cohesion must be in [0..100]");
        this.cohesion = cohesion;
    }

    public void validate() {
        if (state == GroupState.HUDDLING && cohesion < 50) {
            throw new IllegalArgumentException("HUDDLING requires cohesion >= 50");
        }
    }

    public void huddleUp() {
        setCohesion(Math.min(100, cohesion + 30));
        if (cohesion < 50) setCohesion(50);
        state = GroupState.HUDDLING;
        validate();
    }

    public void waitForEnd() {
        setCohesion(Math.min(100, cohesion + 10));
        state = GroupState.WAITING_END;
    }
}