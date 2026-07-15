package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mazie.model.Hero;
import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.WHITE;

public class SelectHeroPanel extends JPanel {

    public SelectHeroPanel(Map<Integer, Hero> heroes, BlockingQueue<Hero> queue) {
        this.setBackground(PURPLE);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout(10, 10));

        final var question = new JLabel("who do you want to be?", JLabel.CENTER);
        question.setForeground(WHITE);
        this.add(question, BorderLayout.NORTH);
        
        final var scrollPane = new JScrollPane(listPanel(heroes, queue));

        this.add(scrollPane, BorderLayout.CENTER); 
    }

    private JPanel listPanel(Map<Integer, Hero> heroes, BlockingQueue<Hero> queue) {
        final var panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(PURPLE);
        
        heroes.values().forEach(hero -> {
            panel.add(heroRow(hero, queue));
        });

        return panel;
    }

    private JPanel heroRow(Hero hero, BlockingQueue<Hero> queue) {
        final var panel = new JPanel();
        panel.setBackground(PURPLE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new HeroPanel(hero), BorderLayout.CENTER);

        final var selectButton = new JButton("select");
        selectButton.setSize(60, 40);
        selectButton.setMargin(new Insets(2, 10, 2, 10));

        selectButton.addActionListener(event -> {
            try {
                queue.put(hero);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("gui view interrupted: " + e.getMessage());
            }
        });

        panel.add(selectButton, BorderLayout.SOUTH);

        return panel;
    }
}
