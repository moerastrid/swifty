package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.concurrent.BlockingQueue;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mazie.model.Hero;

public class ConfirmPanel extends JPanel {

    public ConfirmPanel(Hero hero, BlockingQueue<Boolean> queue) {
        setLayout(new BorderLayout(10, 10));

        final var question = new JLabel("Do you want to start your journey with %s?".formatted(hero.getName()), JLabel.CENTER);
        add(question, BorderLayout.NORTH);
        add(new HeroPanel(hero), BorderLayout.CENTER);
        add(new YesNoButtonPanel(queue), BorderLayout.SOUTH);
    }
}
