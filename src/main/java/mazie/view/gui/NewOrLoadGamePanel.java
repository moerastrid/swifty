package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.concurrent.BlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static mazie.view.gui.ThemeColor.BLACK;
import static mazie.view.gui.ThemeColor.TEAL;
import static mazie.view.gui.ThemeColor.YELLOW;

public class NewOrLoadGamePanel extends JPanel {

    public NewOrLoadGamePanel(BlockingQueue<Boolean> queue) {
        setLayout(new BorderLayout(10, 10));
        setLogo();
        add(buttonPanel(queue), BorderLayout.SOUTH);
    }

    private void setLogo() {
        add(new JLabel(PngMap.getPng(PngMap.ScreenType.LOGO)), BorderLayout.CENTER);
    }

    private JPanel buttonPanel(BlockingQueue<Boolean> queue) {
        final var panel = new JPanel();
        final var newButton = new JButton("new game");
        final var loadButton = new JButton("load game");
        newButton.setSize(60, 60);
        loadButton.setSize(60, 60);
        newButton.setBackground(TEAL);
        newButton.setForeground(BLACK);
        loadButton.setBackground(YELLOW);
        loadButton.setForeground(BLACK);

        newButton.addActionListener(event -> queue.offer(true));
        loadButton.addActionListener(event -> queue.offer(false));

        panel.add(newButton);
        panel.add(loadButton);
        return panel;
    }
}
