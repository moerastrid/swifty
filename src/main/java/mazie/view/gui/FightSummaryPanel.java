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
    private final Monster monster;
    private final Hero hero;
    private final int damageToHero;

    public FightSummaryPanel(Monster monster, Hero hero, CountDownLatch latch, int damageToHero) {
        setBackground(PURPLE);
        setForeground(WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.monster = monster;
        this.hero = hero;
        this.damageToHero = damageToHero;

        add(note());
        add(monsterAction());
        add(heroAction());
        add(new FightScenePanel(monster, hero));
        add(conclusion());

        add(okButton(latch));
    }

    private JLabel note() {
        final var label = new JLabel("You survived %s".formatted(monster.getName()));
        label.setForeground(YELLOW);
        label.setAlignmentX(CENTER_ALIGNMENT);
        final var bigFont = label.getFont().deriveFont(Font.BOLD, 24f);
        label.setFont(bigFont);
        return label;
    }

    private JLabel monsterAction() {
        final var info = "%s: -%d hp".formatted(monster.getAction(), damageToHero);
        final var label = new JLabel(info);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        return label;
    }

    private JLabel heroAction() {
        final var info = "%s: +%d xp".formatted(hero.getAction(), monster.getXpReward());
        final var label = new JLabel(info);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        return label;
    }

    private JLabel conclusion() {
        final var label = new JLabel(monster.getFinalMessage());
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
