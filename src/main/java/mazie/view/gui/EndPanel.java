package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.concurrent.CountDownLatch;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EndPanel extends JPanel {

    public EndPanel(CountDownLatch latch, boolean win) {
        this.setLayout(new BorderLayout(10, 10));

        String text = win ? "you win" : "you lost";
        final var label = new JLabel(text, JLabel.CENTER);
        final var bigFont = label.getFont().deriveFont(Font.BOLD, 24f);
        label.setFont(bigFont);

        this.add(label, BorderLayout.NORTH);

        final var image = win ? PngMap.getPng(PngMap.ScreenType.WIN) : PngMap.getPng(PngMap.ScreenType.GAMEOVER);
        this.add(new JLabel(image), BorderLayout.CENTER);

        final var okButton = new JButton("ok");
        okButton.addActionListener(event -> latch.countDown());
        this.add(okButton, BorderLayout.SOUTH);
    }
}
