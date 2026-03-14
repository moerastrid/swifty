package mazie.model.hero;

import mazie.model.hero.HeroType;

public class Bear extends Hero{

    public Bear(String name) {
        super(name);
        this.type = HeroType.BEAR;
        this.hp = 150;
    }
}
