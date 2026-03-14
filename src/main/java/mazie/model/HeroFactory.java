package mazie.model;

import mazie.model.hero.Hero;
import mazie.model.hero.HeroType;

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
            case "P", "PENGUIN" -> new Hero(name, HeroType.PENGUIN);
            case "F", "FROG" -> new Hero(name, HeroType.FROG);
            case "B", "BEAR" -> new Hero(name, HeroType.BEAR);
            case "H", "HARE" -> new Hero(name, HeroType.HARE);
            case "T", "TURTLE" -> new Hero(name, HeroType.TURTLE);
            default -> throw new IllegalArgumentException("Invalid hero type");
        };
    }
}
