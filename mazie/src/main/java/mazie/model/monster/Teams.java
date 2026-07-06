package mazie.model.monster;

public class Teams extends Monster {

    final static String NAME = "a teams meeting";
    final static String[] ACTIONS = {
        "can everyone hear me?",
        "you're on mute",
        "you accidentally shared the wrong screen",
        "there's an awkward silence after a question.",
        "you raise your hand instead of pressing the thumbs up emoji"
    };
    final static String GOODBYE = "this could have been an email";

    public Teams(int lvl) {
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
