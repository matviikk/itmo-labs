package pokemons;
import ru.ifmo.se.pokemon.*;
import moves.*;

public class Lampent extends Litwick {
    public Lampent(String name, int level) {
        super(name, level);
        super.setStats(60, 40, 60, 95, 60, 55);
        Inferno inferno = new Inferno();
        addMove(inferno);
    }
}
