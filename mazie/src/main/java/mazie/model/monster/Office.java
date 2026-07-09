package mazie.model.monster;

public class Office extends Monster {

    final static String NAME = "your office";
    final static String[] ACTIONS = {
        "another meeting..",
        "the TL-lights are blinding.",
        "oh no, that one coworker is also in the office (we all know who)",
        "someone starts a Teams call without headphones."
    };
    final static String GOODBYE = "17 o'clock, time to leave this place.";

    public Office(int lvl) {
        super(NAME, ACTIONS, GOODBYE, lvl, 14, 3, 19, 74);
    }
}
