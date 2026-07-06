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
        super(NAME, ACTIONS, GOODBYE, calcAttack(lvl), calcDefence(lvl), calcHp(lvl), calcXp(lvl));
    }

    protected static int calcAttack(int lvl) {
        return 24 + (lvl * 14) / 10;
    }

    protected static int calcDefence(int lvl) {
        return 3 + (lvl * 11) / 10;
    }

    protected static int calcHp(int lvl) {
        return 25 + lvl * 9;
    }

    protected static int calcXp(int lvl) {
        return 170 + (lvl / 2 + lvl) * 160;
    }
}
