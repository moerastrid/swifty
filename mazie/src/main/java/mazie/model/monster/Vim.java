package mazie.model.monster;

public class Vim extends Monster {

    final static String NAME = "vim";
    final static String[] ACTIONS = {
        "you google: \"how to leave vim?\"",
        "*restarts pc*",
        "escape?",
        "you accidentally opened another vim.",
        "why would you ever want to leave?"
    };
    final static String GOODBYE = ":wq";

    public Vim(int lvl) {
        super(NAME, ACTIONS, GOODBYE, calcAttack(lvl), calcDefence(lvl), calcHp(lvl), calcXp(lvl));
    }

    protected static int calcAttack(int lvl) {
        return 11 + lvl;
    }

    protected static int calcDefence(int lvl) {
        return 1 + lvl / 2;
    }

    protected static int calcHp(int lvl) {
        return 15 + lvl * 4;
    }

    protected static int calcXp(int lvl) {
        return 60 + (lvl / 2 + lvl) * 95;
    }
}
