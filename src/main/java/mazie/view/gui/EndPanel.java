package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.util.concurrent.CountDownLatch;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EndPanel extends JPanel {

    public EndPanel(CountDownLatch latch, boolean win) {
        this.setBackground(ThemeColor.GREY);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout(10, 10));

        String text = win ? "you win" : "you lost";
        final var label = new JLabel(text, JLabel.CENTER);

        this.add(label, BorderLayout.CENTER);

        final var okButton = new JButton("ok");
        okButton.setBackground(ThemeColor.PURPLE);
        okButton.setForeground(ThemeColor.YELLOW);
        okButton.setMargin(new Insets(2, 10, 2, 10));
        okButton.addActionListener(event -> {
            latch.countDown();
        });
        this.add(okButton, BorderLayout.SOUTH);
    }
}
