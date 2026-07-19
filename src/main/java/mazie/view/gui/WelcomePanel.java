package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.concurrent.CountDownLatch;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WelcomePanel extends JPanel {

    public WelcomePanel(CountDownLatch latch) {
        this.setLayout(new BorderLayout(10, 10));

        final var imageLabel = new JLabel(PngMap.getPng(PngMap.ScreenType.LOGO));
        imageLabel.setBorder(BorderFactory.createEmptyBorder());
        this.add(imageLabel, BorderLayout.CENTER);

        final var startButton = new JButton("Start");
        startButton.addActionListener(event -> latch.countDown());
        this.add(startButton, BorderLayout.SOUTH);
    }
}
