package mazie.model.hero;

import mazie.model.hero.HeroType;

public class Penguin extends Hero {

    public Penguin(String name) {
        super(name);
        this.type = HeroType.PENGUIN;
        this.attack = 16;
    }
}
