package pokemons;
import ru.ifmo.se.pokemon.*;
import moves.*;

public class Chandelure extends Lampent {
    public Chandelure(String name, int level) {
        super(name, level);
        super.setStats(60, 55, 90, 145, 90, 80);
        DoubleTeam doubleTeam = new DoubleTeam();
        addMove(doubleTeam);
    }
}
