package pokemons;
import ru.ifmo.se.pokemon.*;
import moves.*;

public class Zebstrika extends Blitzle {
    public Zebstrika(String name, int level) {
        super(name, level);
        super.setStats(75, 100, 63, 80, 63, 116);
        Overheat overheat = new Overheat();
        addMove(overheat);
    }
}
