package mazie.view.gui;

import java.util.Map;
import javax.swing.ImageIcon;
import mazie.model.HeroType;
import mazie.model.monster.Monster;

public class PngMap {

    private PngMap() {
    }

    private static final Map<HeroType, String> heroPngPath = Map.of(
            HeroType.FROG, "/frog.png",
            HeroType.MOUSE, "/mouse.png",
            HeroType.WEEVIL, "/weevil.png"
    );

    private static ImageIcon loadIcon(String path) {
        final var url = PngMap.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("missing resource: %s".formatted(path));
        }
        return new ImageIcon(url);
    }

    public static ImageIcon getPng(HeroType type) {
        final var path = heroPngPath.get(type);
        if (path == null) {
            throw new IllegalStateException("Unexpected value: %s".formatted(type.name()));
        }
        return loadIcon(path);
    }

    // "your office kanteen", "/kanteen.png", #todo tekenen
    private static final Map<String, String> monsterNamePngPath = Map.ofEntries(
            Map.entry("the bus", "/bus.png"),
            Map.entry("a hairsalon", "/hairsalon.png"),
            Map.entry("IKEA", "/ikea.png"),
            Map.entry("your office kanteen", "/office.png"),
            Map.entry("the library", "/library.png"),
            Map.entry("your office", "/office.png"),
            Map.entry("the park", "/park.png"),
            Map.entry("the supermarket (on a saturday)", "/supermarket.png"),
            Map.entry("an indoor swimming pool", "/swimmingpool.png"),
            Map.entry("a teambuilding event", "/teambuilding.png"),
            Map.entry("a teams meeting", "/teams.png"),
            Map.entry("vim", "/vim.png"));

    public static ImageIcon getPng(Monster monster) {
        final var path = monsterNamePngPath.get(monster.getName());
        if (path == null) {
            throw new IllegalStateException("Unexpected value: %s".formatted(monster.getName()));
        }
        return loadIcon(path);
    }
}
