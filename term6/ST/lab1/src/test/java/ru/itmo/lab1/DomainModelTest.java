package ru.itmo.lab1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.lab1.domain.*;

import static org.junit.jupiter.api.Assertions.*;

public class DomainModelTest {

    @Test
    @DisplayName("Invariant: active bombardment must have intensity > 0")
    void bombardmentInvariant() {
        assertThrows(IllegalArgumentException.class, () -> new Bombardment(true, 0));
        assertDoesNotThrow(() -> new Bombardment(true, 10));
        assertDoesNotThrow(() -> new Bombardment(false, 0));
    }

    @Test
    @DisplayName("Environment bounds: heat/noise must be within [0..100]")
    void environmentBounds() {
        assertThrows(IllegalArgumentException.class, () -> new Environment(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Environment(0, 101));
        assertDoesNotThrow(() -> new Environment(50, 50));
    }

    @Test
    @DisplayName("ComputerBank: integrity and melt percent within [0..100]")
    void computerBankBounds() {
        assertThrows(IllegalArgumentException.class, () -> new ComputerBank(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ComputerBank(0, 200));
        assertDoesNotThrow(() -> new ComputerBank(100, 0));
    }

    @Test
    @DisplayName("MoltenMetalFlow invariants: inactive => direction NONE, thickness 0")
    void moltenFlowInvariantInactive() {
        assertDoesNotThrow(() -> new MoltenMetalFlow(false, Corner.NONE, 0));
        assertThrows(IllegalArgumentException.class, () -> new MoltenMetalFlow(false, Corner.TARGET_CORNER, 0));
        assertThrows(IllegalArgumentException.class, () -> new MoltenMetalFlow(false, Corner.NONE, 2));
    }

    @Test
    @DisplayName("MoltenMetalFlow invariants: active => TARGET_CORNER and thickness in [1..10]")
    void moltenFlowInvariantActive() {
        assertDoesNotThrow(() -> new MoltenMetalFlow(true, Corner.TARGET_CORNER, 5));
        assertThrows(IllegalArgumentException.class, () -> new MoltenMetalFlow(true, Corner.NONE, 5));
        assertThrows(IllegalArgumentException.class, () -> new MoltenMetalFlow(true, Corner.TARGET_CORNER, 0));
    }

    @Test
    @DisplayName("PeopleGroup: HUDDLING requires cohesion >= 50")
    void groupHuddlingRequiresCohesion() {
        assertThrows(IllegalArgumentException.class,
                () -> new PeopleGroup(3, Corner.TARGET_CORNER, 10, GroupState.HUDDLING));
        assertDoesNotThrow(() -> new PeopleGroup(3, Corner.TARGET_CORNER, 50, GroupState.HUDDLING));
    }

    @Test
    @DisplayName("Scene transition: bombardmentResumes increases heat/noise and damages bank")
    void sceneBombardmentResumes() {
        Bombardment b = new Bombardment(false, 0);
        Environment env = new Environment(10, 10);
        ComputerBank bank = new ComputerBank(100, 0);

        SceneService svc = new SceneService();
        svc.bombardmentResumes(b, env, bank, 60);

        assertTrue(b.isActive());
        assertTrue(b.getIntensity() > 0);
        assertTrue(env.getHeatLevel() > 10);
        assertTrue(env.getNoiseLevel() > 10);
        assertTrue(bank.getIntegrity() < 100);
    }

    @Test
    @DisplayName("Scene transition: if front is mostly melted => molten flow activates to corner")
    void sceneMeltingActivatesFlow() {
        ComputerBank bank = new ComputerBank(70, 95);
        MoltenMetalFlow flow = new MoltenMetalFlow(false, Corner.NONE, 0);

        SceneService svc = new SceneService();
        svc.meltingProgress(bank, flow);

        assertTrue(flow.isActive());
        assertEquals(Corner.TARGET_CORNER, flow.getDirection());
        assertTrue(flow.getThickness() >= 1);
    }

    @Test
    @DisplayName("Scene transition: molten metal to corner makes group huddle and wait for end")
    void sceneFlowMakesGroupWaitForEnd() {
        MoltenMetalFlow flow = new MoltenMetalFlow(true, Corner.TARGET_CORNER, 5);
        PeopleGroup group = new PeopleGroup(4, Corner.TARGET_CORNER, 10, GroupState.SITTING);

        SceneService svc = new SceneService();
        svc.moltenMetalApproachesCorner(flow, group);

        assertEquals(GroupState.WAITING_END, group.getState());
        assertTrue(group.getCohesion() >= 50);
    }
}
