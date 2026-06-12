package pokemons;
import ru.ifmo.se.pokemon.*;
import moves.*;

public class Blitzle extends Pokemon {
    public Blitzle(String name, int level) {
        super(name, level);
        super.setStats(45, 60, 32, 50, 32, 76);
        super.setType(Type.ELECTRIC);
        Swagger swagger = new Swagger();
        WildCharge wildcharge = new WildCharge();
        ChargeBeam chargebeam = new ChargeBeam();
        setMove(swagger, wildcharge, chargebeam);
    }
}
