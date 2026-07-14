package mazie.view.gui;


import javax.swing.JButton;

import static mazie.view.gui.ThemeColor.GREEN;
import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.WHITE;

public class YesNoButton extends JButton {

    private final Boolean yes;

    public YesNoButton(Boolean yes) {
        this.yes = yes;
        this.setSize(60, 60);

        final var text = yes ? "yes" : "no";
        this.setText(text);
        
        final var color = yes ? PURPLE : GREEN;
        this.setBackground(color);
        this.setForeground(WHITE);
    }

    public Boolean getYes() {
        return this.yes;
    }
}
