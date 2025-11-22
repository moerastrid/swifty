package ajav.model.hero;

public class Hare extends Hero {

    public Hare(String name) {
        super(name);
        this.type = HeroType.HARE;
        this.levelUp();
        this.levelUp();
    }
}
