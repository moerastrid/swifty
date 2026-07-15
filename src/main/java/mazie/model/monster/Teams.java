package mazie.model.monster;

public class Teams extends Monster {

    final static String NAME = "a teams meeting";
    final static String[] ACTIONS = {
            "someone asks: can everyone hear me?",
            "you're on mute",
            "you accidentally shared the wrong screen",
            "there's an awkward silence after a question.",
            "you raise your hand instead of pressing the thumbs up emoji"
    };
    final static String GOODBYE = "this could have been an email";

    public Teams(int lvl) {
        super(NAME, ACTIONS, GOODBYE, lvl, 13, 3, 16, 73);
    }
}
