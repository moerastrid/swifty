package mazie.view.gui;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final var name = new JLabel(hero.getName());
        name.setAlignmentX(CENTER_ALIGNMENT);
        name.setForeground(PURPLE);
        name.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        this.add(name);

        final var image = PngMap.getSidebarIcon(hero);
        final var imageLabel = new JLabel(image);
        imageLabel.setAlignmentX(CENTER_ALIGNMENT);
        this.add(imageLabel);

        this.add(this.statsPanel(hero));

        final var artifacts = hero.getArtifacts().stream().map(a -> "%s(%d)".formatted(a.name(), a.value())).toList();
        final var info = artifacts.isEmpty() ? "nothing" : String.join("\n - ", artifacts);

        final var current = new JTextArea("\ncurrently wearing:\n\n - %s".formatted(info));
        current.setBackground(GREY);
        current.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(current);
    }

    private JPanel statsPanel(Hero hero) {
        final var panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.setLayout(new GridLayout(5, 2, 1, 2));

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
        final var tag = new JLabel(text);
        tag.setAlignmentX(CENTER_ALIGNMENT);
        tag.setForeground(WHITE);
        return tag;
    }
}
