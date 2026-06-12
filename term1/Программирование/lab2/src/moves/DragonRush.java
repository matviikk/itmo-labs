package moves;
import ru.ifmo.se.pokemon.*;

public class DragonRush extends PhysicalMove {
    public DragonRush() {
        super(Type.DRAGON, 100, 75);
    }

    @Override
    protected void applyOppEffects(Pokemon opponent) {
        super.applyOppEffects(opponent);
        if (Math.random() < 0.2) {  //
            Effect.paralyze(opponent);
        }
    }

    @Override
    protected void applyOppDamage(Pokemon opponent, double damage) {
        opponent.setMod(Stat.HP, (int) damage);
    }

    @Override
    protected String describe() {
        return "uses Dragon Rush";
    }
}
