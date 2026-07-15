package mazie.model.monster;

public class Bus extends Monster {

    final static String NAME = "the bus";
    final static String[] ACTIONS = {
        "all the seats are taken.",
        "it's so busy.",
        "the person behind you is kicking your seat.",
        "you feel carsick",
        "the leg of the person next to you is touching yours :("
    };
    final static String GOODBYE = "you arrive at your stop and leave the bus.";

    public Bus(int lvl) {
        super(NAME, ACTIONS, GOODBYE, lvl, 10, 3, 17, 71);
    }
}
