package mazie.model.monster;

public class Library extends Monster {

    final static String NAME = "the library";
    final static String[] ACTIONS = {
        "you get a fine for handing in your books too late.",
        "there's someone yapping in the quiet area."
    };
    final static String GOODBYE = "you leave the library, once again with more books than you planned on borrowing.";

    public Library(int lvl) {
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
