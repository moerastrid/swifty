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
