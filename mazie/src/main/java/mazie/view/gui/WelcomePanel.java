package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static mazie.view.gui.ThemeColor.GREY;
import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.YELLOW;

public class WelcomePanel extends JPanel {

    public WelcomePanel(CountDownLatch latch) {
        this.setBackground(GREY);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout(10, 10));

        final var image = new ImageIcon(getClass().getResource("/mazie-logo.png"));
        final var imageLabel = new JLabel(image);
        imageLabel.setForeground(PURPLE);
        this.add(imageLabel, BorderLayout.CENTER);

        final var startButton = new JButton("Start");
        startButton.setBackground(PURPLE);
        startButton.setForeground(YELLOW);
        startButton.addActionListener(event -> latch.countDown());
        this.add(startButton, BorderLayout.SOUTH);
    }
}
