package ajav.model.hero;

public class Bear extends Hero{

    public Bear(String name) {
        super(name);
        this.type = HeroType.BEAR;
        this.hp = 150;
    }
}
