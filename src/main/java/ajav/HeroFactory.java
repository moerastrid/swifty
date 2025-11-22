package ajav;

import ajav.model.hero.Hero;
import ajav.model.hero.Penguin;

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
            case "FROG" -> new ajav.model.hero.Frog(name);
            case "BEAR" -> new ajav.model.hero.Bear(name);
            case "HARE" -> new ajav.model.hero.Hare(name);
            case "TURTLE" -> new ajav.model.hero.Turtle(name);
            default -> throw new IllegalArgumentException("Invalid hero type");
        };
    }
}
