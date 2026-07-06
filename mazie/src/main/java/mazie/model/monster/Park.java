package mazie.model.monster;

public class Park extends Monster {

    final static String NAME = "the park";
    final static String[] ACTIONS = {
        "there might be a wasp around, you can't stop thinking about it.",
        "there's a dog barking in the distance.",
        "wait, what's that smell?"
    };
    final static String GOODBYE = "what a lovely time outside, you're sad to go";

    public Park(int lvl) {
        super(NAME, ACTIONS, GOODBYE, calcAttack(lvl), calcDefence(lvl), calcHp(lvl), calcXp(lvl));
    }

    protected static int calcAttack(int lvl) {
        return 17 + (lvl * 12) / 10;
    }

    protected static int calcDefence(int lvl) {
        return 2 + (lvl * 8) / 10;
    }

    protected static int calcHp(int lvl) {
        return 20 + lvl * 6;
    }

    protected static int calcXp(int lvl) {
        return 95 + (lvl / 2 + lvl) * 125;
    }
}
