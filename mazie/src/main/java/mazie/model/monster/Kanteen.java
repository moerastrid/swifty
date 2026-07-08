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
        super(NAME, ACTIONS, GOODBYE, lvl, 14, 5, 32, 90);
    }
}
