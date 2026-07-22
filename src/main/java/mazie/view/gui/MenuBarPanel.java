package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static mazie.view.gui.theme.ThemeColour.BLACK;
import static mazie.view.gui.theme.ThemeColour.GREY;
import static mazie.view.gui.theme.ThemeColour.LILA;
import static mazie.view.gui.theme.ThemeColour.YELLOW;

public class MenuBarPanel extends JPanel {

    private final JLabel log;
    private final JButton switchButton;

    public MenuBarPanel() {
        setBorder(BorderFactory.createEmptyBorder());
        setLayout(new BorderLayout());
        setOpaque(false);

        switchButton = createSwitchButton();
        add(switchButton, BorderLayout.EAST);

        log = createLog();
        add(log, BorderLayout.CENTER);
    }

    public void setSwitchListener(Runnable switchListener) {
        for (var listener : switchButton.getActionListeners()) {
            switchButton.removeActionListener(listener);
        }
        switchButton.addActionListener(event -> switchListener.run());
        revalidate();
        repaint();
    }

    public void setLog(String text) {
        log.setText(text);
        log.setForeground(LILA);
        log.setBackground(GREY);
        log.setOpaque(true);
        revalidate();
        repaint();
    }

    public void setErrorLog(String text) {
        log.setText(text);
        log.setForeground(BLACK);
        log.setBackground(YELLOW);
        log.setOpaque(true);
        revalidate();
        repaint();
    }

    public void clearLog() {
        log.setText("");
        log.setOpaque(false);
        revalidate();
        repaint();
    }

    private JButton createSwitchButton() {
        final var button = new JButton("switch view");
        button.setBackground(YELLOW);
        button.setForeground(BLACK);
        button.setMargin(new Insets(0, 20, 0, 20));
        // button.setPreferredSize(new Dimension(65, 15));
        return button;
    }

    private JLabel createLog() {
        final var label = new JLabel("", JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        return label;
    }
}
