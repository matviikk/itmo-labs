package moves;
import ru.ifmo.se.pokemon.*;

public class Flamethrower extends SpecialMove {
    public Flamethrower() {
        super(Type.FIRE, 90, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon opponent) {
        super.applyOppEffects(opponent);
        if (Math.random() <= 0.1) {
            Effect.burn(opponent);
        }
    }

    @Override
    protected void applyOppDamage(Pokemon opponent, double damage) {
        opponent.setMod(Stat.HP, (int) damage);
    }

    @Override
    protected String describe() {
        return "использует Flamethrower";
    }
}
