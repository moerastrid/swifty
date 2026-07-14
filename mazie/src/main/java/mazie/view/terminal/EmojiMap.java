package mazie.view.terminal;

import java.util.Map;

import mazie.model.HeroType;
import mazie.model.monster.Monster;

public class EmojiMap {
    private EmojiMap() {}

    private static final Map<HeroType, String> heroEmojis = Map.of(
        HeroType.FROG, "🐸",
        HeroType.MOUSE, "🐁",
        HeroType.WEEVIL, "🪲" 
    );

    
    public static String getEmoji(HeroType type) {
        return heroEmojis.get(type);
    }

    public static String getEmoji(Monster monster) {
        return switch (monster.getName()) {
            case "the bus" -> "🚌";
            case "a hairsalon" -> "✂️";
            case "IKEA" -> "🛋️";
            case "your office kanteen" -> "🍽️";
            case "the library" -> "📚";
            case "your office" -> "🏢";
            case "the park" -> "🌳";
            case "the supermarket (on a saturday)" -> "🛒";
            case "an indoor swimming pool" -> "🏊🏻‍♂️";
            case "a teambuilding event" -> "🤼‍♂️";
            case "a teams meeting" -> "👥";
            case "vim" -> "😈";
            default -> throw new IllegalStateException("Unexpected value: " + (monster));
        };
    }
    
}
