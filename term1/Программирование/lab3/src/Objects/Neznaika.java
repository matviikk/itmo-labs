package Objects;

import Exceptions.CustomCheckedException;
import Enums.EnvironmentType;
import Interfaces.Observable;
import java.util.Objects;
import Enums.NeznaikaFeelings;

public class Neznaika {
    private final String name;
    private boolean stumbled;
    private final SpaceSuit spaceSuit;
    private EnvironmentType type;
    private NeznaikaFeelings feeling;
    private final Environment environment;

    public Neznaika(String name, SpaceSuit spaceSuit, Environment environment) {
        this.name = name;
        this.stumbled = false; // Начальная инициализация поля stumbled
        this.spaceSuit = spaceSuit; // Инициализация экземпляра SpaceSuit
        this.type = EnvironmentType.SPACE; // Нет воздуха
        this.feeling = NeznaikaFeelings.NOTHAPPY; // Незнайка не радостный
        this.environment = environment;
    }

    public String getNameInCase(String caseType) {
        switch (caseType) {
            case "nominative": // Именительный падеж
                return name + "а";
            case "genitive": // Родительный падеж
                return name + "и";
            default:
                return name;
        }
    }

    public Observable performSpecialObservation() {
        // Анонимный класс, реализующий интерфейс Observable
        return new Observable() {
            @Override
            public String observe(String surroundings) {
                return getNameInCase("nominative") + " огляделся по сторонам и увидел, что окружен " + surroundings;
            }

            @Override
            public String detectAtmosphere(String observation) {
                type = EnvironmentType.EARTH; // Оказалось, что есть воздух
                return "Заметив, что " + observation + ", " + getNameInCase("nominative") + " сделал вывод, что вокруг имеется атмосфера, то есть воздух.";
            }

            @Override
            public String perceiveAirQuality(String realization) {
                if (!spaceSuit.getActions().isWorn()) {
                    return "Ему даже показалось, что воздух вокруг " + realization;
                } else {
                    return getNameInCase("nominative") + " не может сделать такое наблюдение, так как он все еще в скафандре.";
                }
            }
        };
    }
    public String stumbleAndSitDown(String reason) {
        this.stumbled = true; // Устанавливаем флаг в true
        return "Правда, от " + reason + " ноги у " + getNameInCase("genitive") + " подкосились и он сел прямо на землю.";
    }

    public String takeOffSuitAndFeelAir(String action) {
        SpaceSuit.SpaceSuit_actions actions = this.spaceSuit.getActions();
        String takeOffResult = actions.takeOff(spaceSuit);
        return getNameInCase("nominative") + " " + takeOffResult + " и " + action;
    }

    public String conclusion(String reason){
        return "Но это ему, конечно, только так показалось, потому что " + reason;
    }

    public String heartBeat(String action) {
        if (!this.spaceSuit.getActions().isWorn()) {
            this.feeling = NeznaikaFeelings.HAPPY;
            return action + getNameInCase("nominative") + " почувствовал, что сердце гораздо спокойнее стало биться у него в груди.";
        } else {
            return getNameInCase("nominative") + " не может сделать такое наблюдение, так как он все еще в скафандре.";
        }
    }

    public String feelings() {
        if (this.feeling == NeznaikaFeelings.HAPPY){
            return "На душе сделалось весело и легко.";
        } else {
            return "На душе тоскливо и грустно.";
        }
    }

    public String showEmotions(String action){
        return action + "вовремя спохватился и решил повременить с выражением радости.";
    }

    public String observeAndThink(String string) {
        return string + "оглядеться и выяснить, куда он попал.";
    }

    public String foldSuitAndObserve() throws CustomCheckedException {
        // Локальный класс SuitFolder
        class SuitFolder {
            public String fold(SpaceSuit suit) throws CustomCheckedException {
                return suit.getActions().foldCarefully(suit);
            }
        }

        SuitFolder folder = new SuitFolder();
        String foldResult = folder.fold(spaceSuit);
        String knowAreaResult = environment.gettingToKnowTheArea();

        return foldResult + ", " + getNameInCase("nominative") + " спрятал его под одним из кустов и " + knowAreaResult + ".";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neznaika neznaika = (Neznaika) o;
        return stumbled == neznaika.stumbled && Objects.equals(name, neznaika.name) && Objects.equals(spaceSuit, neznaika.spaceSuit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, stumbled, spaceSuit);
    }

    @Override
    public String toString() {
        return "Neznaika{name=" + name + "}";
    }
}
