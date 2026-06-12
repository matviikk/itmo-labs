import Exceptions.CustomCheckedException;
import Enums.EnvironmentType;
import Objects.Neznaika;
import Objects.Parachute;
import Objects.SpaceSuit;
import Objects.Environment;
import Interfaces.Observable;

public class Main {
    public static void main(String[] args) {
        SpaceSuit spaceSuit = new SpaceSuit("космический скафандр");
        Environment environment = new Environment(EnvironmentType.EARTH);
        Neznaika neznaika = new Neznaika("Незнайк", spaceSuit, environment);
        Parachute parachute = new Parachute("крылатый парашют");
        Parachute.FoldDeploy foldDeploy = parachute.new FoldDeploy(); // Создаем экземпляр FoldDeploy
        Observable specialObservation = neznaika.performSpecialObservation();

        // Использование методов с аргументами
        System.out.println("Но " + neznaika.getNameInCase("nominative") + " опасался напрасно, так как " + foldDeploy.deploy(", который был у него за спиной, замедлил падение."));
        System.out.println(neznaika.stumbleAndSitDown("неожиданного толчка"));
        System.out.println(foldDeploy.fold("автоматически сложился у него за спиной, приняв вид капюшона."));
        System.out.println(specialObservation.observe("кустиками с какими-то крошечными зелеными листиками."));
        System.out.println(specialObservation.detectAtmosphere("листочки на кустах колебались"));
        System.out.println(environment.describeWind("Ведь обычно листья на деревьях колеблются не сами по себе; " +
                "в действительности листья колеблет ветер, а ветер, как теперь всем известно, " +
                "это не что иное, как движение воздуха."));
        System.out.println("Придя к такому умозаключению, " + neznaika.takeOffSuitAndFeelAir("почувствовал, что не только не задыхается, но даже вполне свободно может дышать."));
        System.out.println(specialObservation.perceiveAirQuality("гораздо лучше того, которым он дышал на Земле."));
        System.out.println(neznaika.conclusion("он долго пробыл в скафандре и немного отвык от свежего воздуха."));
        System.out.println(neznaika.heartBeat("Вздохнув полной грудью, "));
        System.out.println(neznaika.feelings());
        System.out.println(neznaika.showEmotions("Он даже хотел засмеяться, но "));
        System.out.println(neznaika.observeAndThink("Прежде всего ему, конечно, следовало "));
        try {
            System.out.println(neznaika.foldSuitAndObserve());
        } catch (CustomCheckedException e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
        }

    }
}


