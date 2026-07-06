package mazie.model.monster;

public class Kanteen extends Monster {

    final static String NAME = "your office kanteen";
    final static String[] ACTIONS = {
        "your lunch spot is already taken.",
        "\"how was your weekend?\"",
        "the person on the other side of the table is touching their teeth with their fork while they eat.",
        "someone microwaved fish."
    };
    final static String GOODBYE = "alright, time to get back to work!";

    public Kanteen(int lvl) {
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
