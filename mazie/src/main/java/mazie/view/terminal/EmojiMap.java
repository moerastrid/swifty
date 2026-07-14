package mazie.view.terminal;

import java.util.Map;

import mazie.model.HeroType;
import mazie.model.monster.Bus;
import mazie.model.monster.Monster;

public class EmojiMap {
    private EmojiMap() {};

    private static final Map<HeroType, String> heroEmojis = Map.of(
        HeroType.FROG, "🐸",
        HeroType.MOUSE, "🐁",
        HeroType.WEEVIL, "🪲" 
    );

    
    public static String getEmoji(HeroType type) {
        return heroEmojis.get(type);
    }

    public static String getEmoji(Monster monster) {
        return switch (monster) {
            case Bus -> "";
            default -> "";
        };
    }
    
}
