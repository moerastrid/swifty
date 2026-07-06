package mazie.model.monster;

public class Supermarket extends Monster {

    final static String NAME = "the supermarket (on a saturday)";
    final static String[] ACTIONS = {
        "the TL-lights are blinding.",
        "the queue takes forever.",
        "the bread is sold out (again)."
    };
    final static String GOODBYE = "with all your groceries and an extra cucumber to be sure, you leave the supermarket.";

    public Supermarket(int lvl) {
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
