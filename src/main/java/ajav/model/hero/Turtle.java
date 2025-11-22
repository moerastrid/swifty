package ajav.model.hero;

public class Turtle extends Hero {

    public Turtle(String name) {
        super(name);
        this.type = HeroType.TURTLE;
        this.defense = 16;
    }
}
