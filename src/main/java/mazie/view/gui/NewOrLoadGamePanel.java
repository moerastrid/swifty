package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.util.concurrent.BlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static mazie.view.gui.ThemeColor.GREY;
import static mazie.view.gui.ThemeColor.TEAL;
import static mazie.view.gui.ThemeColor.YELLOW;

public class NewOrLoadGamePanel extends JPanel {

    public NewOrLoadGamePanel(BlockingQueue<Boolean> queue) {
        this.setBackground(GREY);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout(10, 10));
        this.setLogo();
        this.add(buttonPanel(queue), BorderLayout.SOUTH);
    }

    private void setLogo() {
        final var url = getClass().getResource("/mazie-logo.png");
        if (url != null) {
            final var image = new ImageIcon(url);
            final var imageLabel = new JLabel(image);
            this.add(imageLabel, BorderLayout.CENTER);
        }
    }

    private JPanel buttonPanel(BlockingQueue<Boolean> queue) {
        final var panel = new JPanel();
        panel.setBackground(GREY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final var newButton = new JButton("new game");
        final var loadButton = new JButton("load game");
        newButton.setSize(60, 60);
        loadButton.setSize(60, 60);
        newButton.setBackground(TEAL);
        loadButton.setBackground(YELLOW);
        newButton.setMargin(new Insets(2, 10, 2, 10));
        loadButton.setMargin(new Insets(2, 10, 2, 10));

        newButton.addActionListener(event -> {
            try {
                queue.put(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("gui view interrupted: " + e.getMessage());
            }
        });

        loadButton.addActionListener(event -> {
            try {
                queue.put(false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("gui view interrupted: " + e.getMessage());
            }
        });

        panel.add(newButton);
        panel.add(loadButton);
        return panel;
    }
}
