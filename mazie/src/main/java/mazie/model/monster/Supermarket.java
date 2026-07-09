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
        super(NAME, ACTIONS, GOODBYE, lvl, 16, 4, 26, 102);
    }
}
