package Objects;

public abstract class Equipment {
    private final String name;

    public Equipment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

