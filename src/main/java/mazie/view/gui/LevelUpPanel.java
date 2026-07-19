package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.concurrent.CountDownLatch;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mazie.model.Hero;

import static mazie.view.gui.ThemeColor.BLACK;
import static mazie.view.gui.ThemeColor.GREEN;
import static mazie.view.gui.ThemeColor.YELLOW;

public class LevelUpPanel extends JPanel {

    public LevelUpPanel(Hero hero, CountDownLatch latch) {
        this.setBackground(YELLOW);
        this.setLayout(new BorderLayout());

        final var label = new JLabel("level up", JLabel.CENTER);
        label.setForeground(GREEN);
        final var bigFont = label.getFont().deriveFont(Font.BOLD, 24f);
        label.setFont(bigFont);
        this.add(label, BorderLayout.NORTH);

        final var heroPanel = new HeroPanel(hero);
        this.add(heroPanel, BorderLayout.CENTER);

        final var okButton = new JButton("ok");
        okButton.setForeground(YELLOW);
        okButton.setBackground(BLACK);
        okButton.addActionListener(event -> latch.countDown());
        this.add(okButton, BorderLayout.SOUTH);
    }
}
