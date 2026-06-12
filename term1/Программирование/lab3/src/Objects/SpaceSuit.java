package Objects;

import Exceptions.CustomCheckedException;
import Exceptions.CustomUncheckedException;

import java.util.Objects;
public class SpaceSuit extends Equipment {
    private final SpaceSuit_actions actions;
    public SpaceSuit(String name) {
        super(name);
        this.actions = new SpaceSuit_actions();
    }
    // Статический вложенный класс
    public static class SpaceSuit_actions {
        private boolean isWorn = true; // Изначально скафандр надет
        final boolean isFolded = false; // Изначально скафандр не сложен, тк одет

        public String takeOff(SpaceSuit suit) {
            if (isWorn) {
                isWorn = false; // Снял скафандр
                return "снял с себя " + suit.getName();
            } else {
                throw new CustomUncheckedException("Незнайка уже без скафандра.");
            }
        }

        public String foldCarefully(SpaceSuit suit) throws CustomCheckedException {
            if (isWorn && isFolded) {
                throw new CustomCheckedException("Невозможно сложить скафандр. Он все еще одет или уже сложен.");
            } else {
                return "Аккуратно сложив " + suit.getName();
            }
        }

        public boolean isWorn() {
            return isWorn;
        }
    }
    public SpaceSuit_actions getActions() {
        return this.actions;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpaceSuit spaceSuit = (SpaceSuit) o;
        return getActions().isWorn == spaceSuit.getActions().isWorn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getActions().isWorn);
    }

    @Override
    public String toString() {
        return "SpaceSuit{name=" + getName() + "}";
    }
}
