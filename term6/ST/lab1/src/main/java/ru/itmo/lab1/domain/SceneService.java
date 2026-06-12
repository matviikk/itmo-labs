package ru.itmo.lab1.domain;

import java.util.Objects;

public final class SceneService {

    public void bombardmentResumes(Bombardment bombardment, Environment env, ComputerBank bank, int intensity) {
        Objects.requireNonNull(bombardment);
        Objects.requireNonNull(env);
        Objects.requireNonNull(bank);

        if (intensity <= 0) throw new IllegalArgumentException("intensity must be > 0");

        bombardment.setActive(true);
        bombardment.setIntensity(Math.min(100, intensity));
        bombardment.validate();

        env.increaseHeat(Math.min(30, intensity / 2));
        env.increaseNoise(Math.min(40, intensity / 2));

        bank.damage(Math.min(20, intensity / 5));
    }

    public void meltingProgress(ComputerBank bank, MoltenMetalFlow flow) {
        Objects.requireNonNull(bank);
        Objects.requireNonNull(flow);

        if (bank.isFrontMostlyMelted()) {
            if (!flow.isActive()) {
                flow.activateToCorner(5);
            }
        } else {
            flow.deactivate();
        }
    }

    public void moltenMetalApproachesCorner(MoltenMetalFlow flow, PeopleGroup group) {
        Objects.requireNonNull(flow);
        Objects.requireNonNull(group);

        if (flow.isActive() && flow.getDirection() == Corner.TARGET_CORNER && group.getPosition() == Corner.TARGET_CORNER) {
            group.huddleUp();
            group.waitForEnd();
        }
    }
}