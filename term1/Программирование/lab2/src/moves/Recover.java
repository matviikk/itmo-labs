package moves;
import ru.ifmo.se.pokemon.*;

public class Recover extends StatusMove {
    public Recover() {
        super(Type.NORMAL, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon p) {
        super.applySelfEffects(p);
        p.setMod(Stat.HP, (int)(p.getStat(Stat.HP) / 2));
    }

    @Override
    protected String describe() {
        return "использует Recover";
    }
}
