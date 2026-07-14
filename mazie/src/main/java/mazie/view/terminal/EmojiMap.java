package mazie.view.terminal;

import java.util.Map;

import mazie.model.HeroType;
import mazie.model.monster.Bus;
import mazie.model.monster.Hairsalon;
import mazie.model.monster.IKEA;
import mazie.model.monster.Kanteen;
import mazie.model.monster.Library;
import mazie.model.monster.Monster;
import mazie.model.monster.Office;
import mazie.model.monster.Park;
import mazie.model.monster.Supermarket;
import mazie.model.monster.Swimmingpool;
import mazie.model.monster.TeamBuilding;
import mazie.model.monster.Teams;
import mazie.model.monster.Vim;

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
        return switch (monster) {
            case Bus _ -> "🚌";
            case Hairsalon _ -> "✂️";
            case IKEA _ -> "🛋️";
            case Kanteen _ -> "🍽️";
            case Library _ -> "📚";
            case Office _ -> "🏢";
            case Park _ -> "🌳";
            case Supermarket _ -> "🛒";
            case Swimmingpool _ -> "🏊🏻‍♂️";
            case TeamBuilding _ -> "🤼‍♂️";
            case Teams _ -> "👥";
            case Vim _ -> "⌨️";
            default -> throw new IllegalStateException("Unexpected value: " + (monster));
        };
    }
    
}
