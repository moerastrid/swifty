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
        super(NAME, ACTIONS, GOODBYE, lvl, 5 * lvl, lvl, 5 * lvl, 100 * lvl);
    }
}
