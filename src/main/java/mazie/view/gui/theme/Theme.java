package mazie.view.gui.theme;

import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

import static mazie.view.gui.theme.ThemeColour.BLACK;
import static mazie.view.gui.theme.ThemeColour.GREY;
import static mazie.view.gui.theme.ThemeColour.LILA;
import static mazie.view.gui.theme.ThemeColour.PURPLE;
import static mazie.view.gui.theme.ThemeColour.WHITE;
import static mazie.view.gui.theme.ThemeColour.YELLOW;
import static mazie.view.gui.theme.ThemeFont.INPUT;
import static mazie.view.gui.theme.ThemeFont.TEXT;

public class Theme {

    private static final Border PANEL_BORDER = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    public static final Border TEXT_AREA_BORDER = BorderFactory.createEmptyBorder(4, 4, 4, 4);
    private static final Insets BUTTON_MARGIN = new Insets(2, 10, 2, 10);

    private Theme() {
    }

    public static void configure() {
        UIManager.put("Panel.background", GREY);
        UIManager.put("Panel.foreground", BLACK);
        UIManager.put("Panel.border", PANEL_BORDER);

        UIManager.put("TextArea.background", GREY);
        UIManager.put("TextArea.foreground", BLACK);
        UIManager.put("TextArea.border", TEXT_AREA_BORDER);
        UIManager.put("TextArea.font", TEXT);

        UIManager.put("Button.background", PURPLE);
        UIManager.put("Button.foreground", YELLOW);
        UIManager.put("Button.margin", BUTTON_MARGIN);
        UIManager.put("Button.select", LILA);
        UIManager.put("Button.font", INPUT);

        UIManager.put("Label.foreground", WHITE);
        UIManager.put("Label.border", PANEL_BORDER);
        UIManager.put("Label.font", TEXT);

        UIManager.put("ToggleButton.background", GREY);
        UIManager.put("ToggleButton.foreground", LILA);
        UIManager.put("ToggleButton.margin", BUTTON_MARGIN);
        UIManager.put("ToggleButton.select", YELLOW);
        UIManager.put("ToggleButton.font", INPUT);
    }
}
