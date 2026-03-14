package mazie.model.hero;

import mazie.model.hero.HeroType;

public class Turtle extends Hero {

    public Turtle(String name) {
        super(name);
        this.type = HeroType.TURTLE;
        this.defense = 16;
    }
}
