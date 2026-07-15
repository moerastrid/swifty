package mazie.view.gui;

import java.awt.Insets;
import javax.swing.JButton;

import static mazie.view.gui.ThemeColor.BLACK;
import static mazie.view.gui.ThemeColor.GREEN;
import static mazie.view.gui.ThemeColor.PURPLE;

public class YesNoButton extends JButton {

    private final Boolean yes;

    public YesNoButton(Boolean yes) {
        this.yes = yes;
        this.setSize(60, 60);

        final var text = yes ? "yes" : "no";
        this.setText(text);

        final var color = yes ? PURPLE : GREEN;
        this.setBackground(color);
        this.setForeground(BLACK);
        this.setMargin(new Insets(2, 10, 2, 10));
    }

    public Boolean getYes() {
        return this.yes;
    }
}
