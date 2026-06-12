package pokemons;
import ru.ifmo.se.pokemon.*;
import moves.*;

public class Lugia extends Pokemon {
    public Lugia(String name, int level) {
        super(name, level);
        super.setStats(106, 90, 130, 90, 154, 110);
        super.setType(Type.PSYCHIC, Type.FLYING);
        DragonRush dragonrush = new DragonRush();
        Recover recover = new Recover();
        Bulldoze bulldoze = new Bulldoze();
        Swagger swagger = new Swagger();
        setMove(dragonrush, recover, bulldoze, swagger);
    }
}
