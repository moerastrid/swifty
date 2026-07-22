package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.concurrent.CountDownLatch;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static mazie.view.gui.theme.ThemeFont.HEADER;

public class EndPanel extends JPanel {

    public EndPanel(CountDownLatch latch, boolean win) {
        setLayout(new BorderLayout(10, 10));

        String text = win ? "you win" : "you lost";
        final var label = new JLabel(text, JLabel.CENTER);
        label.setFont(HEADER);

        add(label, BorderLayout.NORTH);

        final var image = win ? PngMap.getPng(PngMap.ScreenType.WIN) : PngMap.getPng(PngMap.ScreenType.GAMEOVER);
        add(new JLabel(image), BorderLayout.CENTER);

        final var okButton = new JButton("ok");
        okButton.addActionListener(event -> latch.countDown());
        add(okButton, BorderLayout.SOUTH);
    }
}
