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
        super(NAME, ACTIONS, GOODBYE, lvl, 19, 1, 9, 60);
    }

}
