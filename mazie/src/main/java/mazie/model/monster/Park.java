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
        super(NAME, ACTIONS, GOODBYE, lvl, 12, 3, 20, 55);
    }
}
