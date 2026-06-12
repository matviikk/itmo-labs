package pokemons;
import ru.ifmo.se.pokemon.*;
import moves.*;

public class Litwick extends Pokemon {
    public Litwick(String name, int level) {
        super(name, level);
        super.setStats(50, 30, 55, 65, 55, 20);
        super.setType(Type.GHOST, Type.FIRE);
        Flamethrower flamethrower = new Flamethrower();
        Facade facade = new Facade();
        setMove(flamethrower, facade);
    }
}
