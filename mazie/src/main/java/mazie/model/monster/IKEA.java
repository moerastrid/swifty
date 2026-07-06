package mazie.model.monster;

public class IKEA extends Monster {

    final static String NAME = "IKEA";
    final static String[] ACTIONS = {
        "the shortcut wasn't.",
        "you forgot why you came here.",
        "you bought candles."
    };
    final static String GOODBYE = "you leave with candles.";

    public IKEA(int lvl) {
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
