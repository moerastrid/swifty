package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mazie.model.Hero;
import static mazie.view.gui.ThemeColor.YELLOW;

public class LevelUpPanel extends JPanel {
    
    public LevelUpPanel(Hero hero, CountDownLatch latch) {
        this.setBackground(YELLOW);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout());

        final var label = new JLabel("level up", JLabel.CENTER);
        this.add(label, BorderLayout.NORTH);

        final var heroPanel = new HeroPanel(hero);
        this.add(heroPanel, BorderLayout.CENTER);

        final var okButton = new JButton("ok");
        okButton.addActionListener(event -> latch.countDown());
        this.add(okButton, BorderLayout.SOUTH);
    }
}
