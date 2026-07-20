package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import mazie.model.Hero;

import static mazie.view.gui.theme.ThemeColor.LILA;
import static mazie.view.gui.theme.ThemeColor.PURPLE;
import static mazie.view.gui.theme.ThemeColor.WHITE;

public class SelectHeroPanel extends JPanel {

    public SelectHeroPanel(Map<Integer, Hero> heroes, BlockingQueue<Hero> queue) {
        this.setBackground(PURPLE);
        this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        this.setLayout(new BorderLayout(10, 10));

        this.add(new JLabel("choose your fighter", JLabel.CENTER), BorderLayout.NORTH);

        final var scrollPane = new JScrollPane(listPanel(heroes, queue));
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel listPanel(Map<Integer, Hero> heroes, BlockingQueue<Hero> queue) {
        final var panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder());
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        heroes.values().forEach(hero -> panel.add(heroCard(hero, queue)));

        return panel;
    }

    private JPanel heroCard(Hero hero, BlockingQueue<Hero> queue) {
        final var panel = new JPanel();
        panel.setBackground(PURPLE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        panel.add(new HeroPanel(hero), BorderLayout.CENTER);

        final var selectButton = selectButton(hero, queue);

        panel.add(selectButton, BorderLayout.SOUTH);

        return panel;
    }

    private JButton selectButton(Hero hero, BlockingQueue<Hero> queue) {
        final var selectButton = new JButton("select");
        selectButton.setForeground(LILA);
        selectButton.setBackground(WHITE);
        selectButton.addActionListener(event -> queue.offer(hero));
        return selectButton;
    }
}
