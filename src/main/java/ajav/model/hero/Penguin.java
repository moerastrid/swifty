package ajav.model.hero;

public class Penguin extends Hero {

    public Penguin(String name) {
        super(name);
        this.type = HeroType.PENGUIN;
        this.attack = 16;
    }
}
