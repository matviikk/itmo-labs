package Objects;

import Enums.EnvironmentType;
import Exceptions.CustomCheckedException;
import Exceptions.CustomUncheckedException;

public class Environment {
    private final EnvironmentType type;

    public Environment(EnvironmentType type) {
        this.type = type;
    }
    public String describeWind(String msg) {
        if (type != EnvironmentType.EARTH){
            throw new CustomUncheckedException("Мы не на Земле:(");
        }
        return msg;
    }

    public String gettingToKnowTheArea() throws CustomCheckedException{
        if (type != EnvironmentType.EARTH){
            throw new CustomCheckedException("Мы не на Земле:(");
        } else {
            return "принялся знакомиться с местностью";
        }
    }

    @Override
    public String toString() {
        return "Environment{type=" + type + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Environment that = (Environment) o;

        return type == that.type;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
