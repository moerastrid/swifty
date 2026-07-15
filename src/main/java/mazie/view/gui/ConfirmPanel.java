package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.concurrent.BlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mazie.model.Hero;

public class ConfirmPanel extends JPanel {

    public ConfirmPanel(Hero hero, BlockingQueue<Boolean> queue) {
        this.setBackground(ThemeColor.GREY);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout(10, 10));

        final var question = new JLabel("confirm hero?", JLabel.CENTER);
        question.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(question, BorderLayout.NORTH);
        this.add(new HeroPanel(hero), BorderLayout.CENTER);
        this.add(new YesNoButtonPanel(queue), BorderLayout.SOUTH);
    }
}
