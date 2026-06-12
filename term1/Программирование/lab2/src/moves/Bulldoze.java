package moves;
import ru.ifmo.se.pokemon.*;

public class Bulldoze extends PhysicalMove {
    public Bulldoze() {
        super(Type.GROUND, 60, 100);
    }

    @Override
    protected void applyOppDamage(Pokemon opponent, double damage) {
        opponent.setMod(Stat.HP, (int) damage);
    }
    @Override
    protected void applyOppEffects(Pokemon opponent) {
        super.applyOppEffects(opponent);
        opponent.setMod(Stat.SPEED, -1);
    }

    @Override
    protected String describe() {
        return "использует Bulldoze";
    }
}
