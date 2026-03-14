package mazie.model.hero;

import mazie.model.hero.HeroType;

public class Hare extends Hero {

    public Hare(String name) {
        super(name);
        this.type = HeroType.HARE;
        this.levelUp();
        this.levelUp();
    }
}
