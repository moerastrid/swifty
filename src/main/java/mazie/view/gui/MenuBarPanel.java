package mazie.view.gui;

import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JPanel;

import static mazie.view.gui.ThemeColor.BLACK;
import static mazie.view.gui.ThemeColor.YELLOW;

public class MenuBarPanel extends JPanel {

    public MenuBarPanel(Runnable switchListener) {
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.setOpaque(false);
        this.add(switchButton(switchListener));
    }
    private JButton switchButton(Runnable switchListener) {
        JButton button = new JButton("switch view");
        button.setBackground(YELLOW);
        button.setForeground(BLACK);
        button.setMargin(new Insets(0, 10, 0, 10));
        button.addActionListener(event -> switchListener.run());
        return button;
    }
}
