package moves;
import ru.ifmo.se.pokemon.*;

public class Overheat extends SpecialMove {
    public Overheat() {
        super(Type.FIRE, 130, 90);
    }

    @Override
    protected void applySelfEffects(Pokemon p) {
        super.applySelfEffects(p);
        Effect effect = new Effect().stat(Stat.SPECIAL_ATTACK, -2);
        p.addEffect(effect);;
    }

    @Override
    protected String describe() {
        return "использует Overheat";
    }
}
