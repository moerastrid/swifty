package mazie.model.monster;

public class TeamBuilding extends Monster {

    final static String NAME = "a teambuilding event";
    final static String[] ACTIONS = {
        "there's an icebreaker activity.",
        "you have to introduce yourself.",
        "let's split up in little groups and talk through each other.",
        "now rate your feelings on a scale of 1 to 10."
    };
    final static String GOODBYE = "the event got extended during lunch, but is over now. you question why you ever started working in corporate.";

    public TeamBuilding(int lvl) {
        super(NAME, ACTIONS, GOODBYE, calcAttack(lvl), calcDefence(lvl), calcHp(lvl), calcXp(lvl));
    }

    protected static int calcAttack(int lvl) {
        return 24 + (lvl * 14) / 10;
    }

    protected static int calcDefence(int lvl) {
        return 3 + (lvl * 11) / 10;
    }

    protected static int calcHp(int lvl) {
        return 25 + lvl * 9;
    }

    protected static int calcXp(int lvl) {
        return 170 + (lvl / 2 + lvl) * 160;
    }
}
