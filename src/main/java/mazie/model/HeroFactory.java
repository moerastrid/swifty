package mazie.model;

import mazie.model.hero.Bear;
import mazie.model.hero.Frog;
import mazie.model.hero.Hare;
import mazie.model.hero.Hero;
import mazie.model.hero.Penguin;
import mazie.model.hero.Turtle;

public class HeroFactory {
    private static final HeroFactory instance = new HeroFactory();

    private HeroFactory() {}

    public static HeroFactory getInstance() {
        return instance;
    }

    public Hero newHero(String type, String name) {
        if (type == null) {
            throw new IllegalArgumentException("Hero type cannot be null");
        }
        return switch (type.toUpperCase()) {
            case "PENGUIN" -> new Penguin(name);
            case "FROG" -> new Frog(name);
            case "BEAR" -> new Bear(name);
            case "HARE" -> new Hare(name);
            case "TURTLE" -> new Turtle(name);
            default -> throw new IllegalArgumentException("Invalid hero type");
        };
    }
}
