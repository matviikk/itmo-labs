import ru.ifmo.se.pokemon.*;
import pokemons.*;

public class Main {
    public static void main(String[] args) {

        Pokemon lugia = new Lugia("Lugia", 3);
        Pokemon blitzle = new Blitzle("Blitzle", 3);
        Pokemon zebstrika = new Zebstrika("Zebstrika", 3);
        Pokemon litwick = new Litwick("Litwick", 3);
        Pokemon lampent = new Lampent("Lampent", 3);
        Pokemon chandelure = new Chandelure("Chandelure", 3);

        Battle battle = new Battle();


        battle.addAlly(lugia);
        battle.addAlly(blitzle);
        battle.addAlly(zebstrika);

        battle.addFoe(litwick);
        battle.addFoe(lampent);
        battle.addFoe(chandelure);

        battle.go();
    }
}

