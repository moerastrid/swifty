package mazie.model.monster;

public class Swimmingpool extends Monster {

    final static String NAME = "an indoor swimming pool";
    final static String[] ACTIONS = {
        "the air feels sticky and moist.",
        "everyone is screaming?!",
        "someone peed in the pool."
    };
    final static String GOODBYE = "wet and tired, you manage to exit the pool.";

    public Swimmingpool(int lvl) {
        super(NAME, ACTIONS, GOODBYE, lvl, 19, 2, 29, 160);
    }
}
