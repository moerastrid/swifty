package mazie.view.gui.theme;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

public final class ThemeFont {

    public static final Font HEADER = new Font(selectFont("Lucida Console", "DejaVu Math TeX Gyre", "Serif"), Font.BOLD, 24);
    public static final Font INPUT = new Font(selectFont("Lucida Console", "DialogInput", "Monospaced"), Font.PLAIN, 14);
    public static final Font TEXT = new Font(selectFont("Lucida Console", "Dialog", "SansSerif"), Font.PLAIN, 14);

    private ThemeFont() {}

    private static boolean isFontAvailable(String fontName) {
        final var fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        return Arrays.asList(fonts).contains(fontName);
    }

    private static String selectFont(String preferred, String alsoGood, String backup) {
        if (isFontAvailable(preferred)) {
            return preferred;
        } else if (isFontAvailable(alsoGood)) {
            return alsoGood;
        }
        return backup;
    }
}
