package moves;
import ru.ifmo.se.pokemon.*;

public class Inferno extends SpecialMove {
    public Inferno() {
        super(Type.FIRE, 100, 50);
    }

    @Override
    protected void applyOppEffects(Pokemon opponent) {
        super.applyOppEffects(opponent);
        Effect.burn(opponent);
    }

    @Override
    protected void applyOppDamage(Pokemon opponent, double damage) {
        opponent.setMod(Stat.HP, (int) damage);
    }

    @Override
    protected String describe() {
        return "использует Inferno";
    }
}
