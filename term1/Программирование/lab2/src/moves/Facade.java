package moves;
import ru.ifmo.se.pokemon.*;

public class Facade extends PhysicalMove {
    public Facade() {
        super(Type.NORMAL, 70, 100);
    }

    @Override
    protected void applyOppDamage(Pokemon opponent, double damage) {
        Status attStatus = opponent.getCondition();
        if (attStatus.equals(Status.BURN) || attStatus.equals(Status.PARALYZE) || attStatus.equals(Status.POISON)) {
            opponent.setMod(Stat.HP, (int) Math.round(damage) * 2);
        } else {
            opponent.setMod(Stat.HP, (int) Math.round(damage));

        }
    }


    @Override
    protected String describe() {
        return "использует Facade";
    }
}
