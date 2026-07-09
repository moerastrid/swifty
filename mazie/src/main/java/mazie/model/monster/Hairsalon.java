package mazie.model.monster;

public class Hairsalon extends Monster {

    final static String NAME = "a hairsalon";
    final static String[] ACTIONS = {
        "the hairdresser tries to make smalltalk.",
        "there are itcy little hair cuttings in your neck.",
        "you're shown the back of your head and are expected to have an opinion."
    };
    final static String GOODBYE = "you leave with a new hairdo, not entirely sure if you like it yet.";

    public Hairsalon(int lvl) {
        super(NAME, ACTIONS, GOODBYE, lvl, 12, 3, 18, 70);
    }
}
