package mazie.view.gui;

import java.awt.Image;
import java.util.Map;
import javax.swing.ImageIcon;
import mazie.model.Hero;
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

    private static ImageIcon scale(ImageIcon icon, int width, int height) {
        final var image = icon.getImage();
        final var scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private static ImageIcon getPng(Hero hero) {
        final var type = hero.getType();
        final var path = heroPngPath.get(type);
        if (path == null) {
            throw new IllegalStateException("Unexpected value: %s".formatted(type.name()));
        }
        return loadIcon(path);
    }

    public static ImageIcon getSidebarIcon(Hero hero) {
        final var png = getPng(hero);
        final var width = png.getIconWidth();
        final var height = png.getIconHeight();
        final var maxHeight = 150;

        final var factor = (double) maxHeight / (double) height;
        final var newWidth = (int) ((double) width * factor);

        System.out.println("width: " + width);
        System.out.println("height: " + height);
        System.out.println("maxHeight: " + maxHeight);
        System.out.println("factor: " + factor);
        System.out.println("newWidth: " + newWidth);

        return scale(png, newWidth, maxHeight);
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
