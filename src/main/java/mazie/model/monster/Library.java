package mazie.model.monster;

public class Library extends Monster {

    final static String NAME = "the library";
    final static String[] ACTIONS = {
        "you get a fine for handing in your books too late.",
        "there's someone yapping in the quiet area."
    };
    final static String GOODBYE = "you leave the library, once again with more books than you planned on borrowing.";

    public Library(int lvl) {
        super(NAME, ACTIONS, GOODBYE, lvl, 6, 2, 10, 40);
    }
}
