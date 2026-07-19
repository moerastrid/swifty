package mazie.view.gui;

import java.awt.Font;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mazie.model.Hero;
import mazie.model.monster.Monster;

import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.WHITE;
import static mazie.view.gui.ThemeColor.YELLOW;

public class FightSummaryPanel extends JPanel {
    private static final Random RANDOM = new Random();

    public FightSummaryPanel(Monster monster, Hero hero, CountDownLatch latch, int damageToHero) {
        this.setBackground(PURPLE);
        this.setForeground(WHITE);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(note(monster));
        this.add(monsterAction(monster, damageToHero));
        this.add(heroAction(hero, monster));
        this.add(new FightScenePanel(monster, hero));
        this.add(conclusion(monster));

        this.add(okButton(latch));
    }

    private JLabel note(Monster monster) {
        final var label = new JLabel("You survived %s".formatted(monster.getName()));
        label.setForeground(YELLOW);
        label.setAlignmentX(CENTER_ALIGNMENT);
        final var bigFont = label.getFont().deriveFont(Font.BOLD, 24f);
        label.setFont(bigFont);
        return label;
    }

    private JLabel monsterAction(Monster monster, int damageToHero) {
        final var info = "%s: -%d hp".formatted(monster.getAction(), damageToHero);
        final var label = new JLabel(info);
        label.setForeground(WHITE);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        return label;
    }

    private JLabel heroAction(Hero hero, Monster monster) {
        final var info = "%s: +%d xp".formatted(hero.getAction(), monster.getXpReward());
        final var label = new JLabel(info);
        label.setForeground(WHITE);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        return label;
    }

    private JLabel conclusion(Monster monster) {
        final var label = new JLabel(monster.getFinalMessage());
        label.setForeground(WHITE);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        return label;
    }

    private JButton okButton(CountDownLatch latch) {
        final var buttonTexts = List.of("ok", "alright", "OK", "whatever", "yes", "understandable", "si claro");
        final var button = new JButton(buttonTexts.get(RANDOM.nextInt(buttonTexts.size())));
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.addActionListener(event -> latch.countDown());
        return (button);
    }
}
