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
