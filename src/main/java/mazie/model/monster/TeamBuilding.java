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
        super(NAME, ACTIONS, GOODBYE, lvl, 19, 4, 25, 104);
    }
}
