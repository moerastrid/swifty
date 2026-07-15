package mazie.model.monster;

public class Swimmingpool extends Monster {

    final static String NAME = "an indoor swimming pool";
    final static String[] ACTIONS = {
            "the air feels sticky and moist.",
            "everyone is screaming?!",
            "water came into your nose.",
            "someone peed in the pool."
    };
    final static String GOODBYE = "wet and tired, you manage to exit the pool.";

    public Swimmingpool(int lvl) {
        super(NAME, ACTIONS, GOODBYE, lvl, 17, 4, 24, 100);
    }
}
