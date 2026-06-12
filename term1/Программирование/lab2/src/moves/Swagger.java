package moves;
import ru.ifmo.se.pokemon.*;

public class Swagger extends StatusMove {
    public Swagger() {
        super(Type.NORMAL, 0, 85);
    }

    @Override
    protected void applyOppEffects(Pokemon opponent) {
        super.applyOppEffects(opponent);
        Effect effect = new Effect().stat(Stat.ATTACK, 2);
        opponent.addEffect(effect);
        opponent.confuse();
    }

    @Override
    protected String describe() {
        return "использует Swagger";
    }
}
