package mazie.view.gui;

import java.awt.Image;
import java.util.Map;
import javax.swing.ImageIcon;
import mazie.model.Hero;
import mazie.model.HeroType;
import mazie.model.monster.Monster;

public class PngMap {

    public enum ScreenType {
        LOGO,
        ICON,
        WHERE,
        HUH,
        WIN,
        GAMEOVER
    }

    private static final Map<ScreenType, String> screenPngPath = Map.of(
            ScreenType.LOGO, "/mazie-logo.png",
            ScreenType.ICON,"/mazie-icon.png",
            ScreenType.WHERE,"/where.png",
            ScreenType.HUH, "/huh.png",
            ScreenType.WIN,"/win.png",
            ScreenType.GAMEOVER,"/gameover.png"
    );

    private static final Map<HeroType, String> heroPngPath = Map.of(
            HeroType.FROG, "/frog.png",
            HeroType.MOUSE, "/mouse.png",
            HeroType.WEEVIL, "/weevil.png"
    );
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

    private PngMap() {
    }

    private static ImageIcon loadIcon(String path) {
        final var url = PngMap.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("missing resource: %s".formatted(path));
        }
        return new ImageIcon(url);
    }

    private static ImageIcon scale(ImageIcon icon, int width, int height) {
        final var image = icon.getImage();
        final var scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public static ImageIcon getPng(HeroType type) {
        final var path = heroPngPath.get(type);
        if (path == null) {
            throw new IllegalStateException("Unexpected value: %s".formatted(type.name()));
        }
        return loadIcon(path);
    }

    public static ImageIcon getButtonIcon(HeroType type) {
        final var png = getPng(type);

        final var maxHeight = 100.0;

        final double factor = maxHeight / png.getIconHeight();
        final var newWidth = png.getIconWidth() * factor;

        return scale(png, (int) newWidth, (int) maxHeight);
    }

    public static ImageIcon getSidebarIcon(Hero hero) {
        final var png = getPng(hero.getType());
        final var width = png.getIconWidth();
        final var height = png.getIconHeight();
        final var maxHeight = 150;

        final var factor = (double) maxHeight / (double) height;
        final var newWidth = (int) ((double) width * factor);

        return scale(png, newWidth, maxHeight);
    }

    public static ImageIcon getPng(Monster monster) {
        final var path = monsterNamePngPath.get(monster.getName());
        if (path == null) {
            throw new IllegalStateException("Unexpected value: %s".formatted(monster.getName()));
        }
        return loadIcon(path);
    }

    public static ImageIcon getPng(ScreenType screen) {
        final var path = screenPngPath.get(screen);
        if (path == null) {
            throw new IllegalStateException("Unexpected value: %s".formatted(screen.name()));
        }
        return loadIcon(path);
    }
}
