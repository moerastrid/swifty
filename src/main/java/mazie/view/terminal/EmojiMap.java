package mazie.view.terminal;

import java.util.Map;
import mazie.model.HeroType;
import mazie.model.monster.Monster;

public class EmojiMap {

    private EmojiMap() {
    }

    private static final Map<HeroType, String> HERO_TYPE_EMOJI = Map.of(
            HeroType.FROG, "🐸",
            HeroType.MOUSE, "🐁",
            HeroType.WEEVIL, "🪲"
    );

    public static String getEmoji(HeroType type) {
        final var emoji = HERO_TYPE_EMOJI.get(type);
        if (emoji == null) {
            throw new IllegalStateException("Unexpected value: %s".formatted(type.name()));
        }
        return emoji;
    }

    private static final Map<String, String> MONSTER_NAME_EMOJI = Map.ofEntries(
            Map.entry("the bus", "🚌"),
            Map.entry("a hairsalon", "✂️"),
            Map.entry("IKEA", "🛋️"),
            Map.entry("your office kanteen", "🍽️"),
            Map.entry("the library", "📚"),
            Map.entry("your office", "🏢"),
            Map.entry("the park", "🌳"),
            Map.entry("the supermarket (on a saturday)", "🛒"),
            Map.entry("an indoor swimming pool", "🏊🏻‍♂️"),
            Map.entry("a teambuilding event", "🤼‍♂️"),
            Map.entry("a teams meeting", "👥"),
            Map.entry("vim", "😈"));

    public static String getEmoji(Monster monster) {
        final var emoji = MONSTER_NAME_EMOJI.get(monster.getName());
        if (emoji == null) {
            throw new IllegalStateException("Unexpected value: %s".formatted(monster.getName()));
        }
        return emoji;
    }

}
