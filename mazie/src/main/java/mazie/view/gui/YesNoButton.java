package mazie.view.gui;

import javax.swing.JButton;

public class YesNoButton extends JButton {

    private final Boolean yes;

    public YesNoButton(Boolean yes) {
        this.yes = yes;
        this.setSize(60, 60);

        final var text = yes ? "yes" : "no";
        this.setText(text);
        
        final var color = yes ? ThemeColor.PURPLE : ThemeColor.GREEN;
        this.setBackground(color);
        this.setForeground(ThemeColor.WHITE);
    }

    public Boolean getYes() {
        return this.yes;
    }
}
