package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import mazie.model.Hero;
import static mazie.view.gui.ThemeColor.GREY;
import static mazie.view.gui.ThemeColor.LILA;
import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.WHITE;

public class HeroPanel extends JPanel {
    
    public HeroPanel(Hero hero) {
        this.setBackground(GREY);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout());

        final var name = new JLabel(hero.getName(), JLabel.CENTER);
        name.setForeground(PURPLE);
        name.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(name, BorderLayout.NORTH);

        this.add(this.statsPanel(hero), BorderLayout.CENTER);

        final var artifacts = hero.getArtifacts().stream().map(a -> "%s(%d)".formatted(a.name(), a.value())).toList();

        final var info = artifacts.isEmpty() ? "nothing" : String.join("\n - ", artifacts);
        final var current = new JTextArea("\ncurrently wearing:\n\n - %s".formatted(info));
        current.setBackground(GREY);
        current.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(current, BorderLayout.SOUTH);
    }

    private JPanel statsPanel(Hero hero) {
        final var panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridLayout(5, 2, 10, 5));

        panel.setBackground(LILA);
        panel.setForeground(WHITE);

        // level
        panel.add(tag("level"));
        panel.add(tag(Integer.toString(hero.getLevel())));

        // xp
        panel.add(tag("xp"));
        panel.add(tag(Integer.toString(hero.getXp())));

        // attack
        panel.add(tag("attack"));
        panel.add(tag(hero.getAttackString()));

        // defence
        panel.add(tag("defence"));
        panel.add(tag(hero.getDefenceString()));

        // hp
        panel.add(tag("hp"));
        panel.add(tag(hero.getHpString()));

        return panel;
    }

    private JLabel tag(String text) {
        final var tag = new JLabel(text, JLabel.CENTER);
        tag.setForeground(WHITE);
        return tag;
    }
}
