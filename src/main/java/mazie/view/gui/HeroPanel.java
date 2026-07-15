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

        this.add(this.nameLabel(hero.getName()));
        this.add(this.imageLabel(hero));
        this.add(this.statsPanel(hero));
        this.add(this.artifactTextArea(hero));
    }

    private JLabel nameLabel(String name) {
        final var label = new JLabel(name);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setForeground(PURPLE);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    private JLabel imageLabel(Hero hero) {
        final var image = PngMap.getSidebarIcon(hero);
        final var label = new JLabel(image);
        label.setAlignmentX(CENTER_ALIGNMENT);
        return label;
    }

    private JTextArea artifactTextArea(Hero hero) {
        final var artifacts = hero.getArtifacts().stream().map(a -> "%s(%d)".formatted(a.name(), a.value())).toList();
        final var info = artifacts.isEmpty() ? "nothing" : String.join("\n - ", artifacts);

        final var textArea = new JTextArea("\ncurrently wearing:\n\n - %s".formatted(info));
        textArea.setBackground(GREY);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    private JPanel statsPanel(Hero hero) {
        final var panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.setLayout(new GridLayout(5, 2, 1, 2));

        panel.setBackground(LILA);
        panel.setForeground(WHITE);

        this.addStat(panel, "level", String.valueOf(hero.getLevel()));
        this.addStat(panel, "xp", String.valueOf(hero.getXp()));
        this.addStat(panel, "attack", hero.getAttackString());
        this.addStat(panel, "defence", hero.getDefenceString());
        this.addStat(panel, "hp", hero.getHpString());

        return panel;
    }

    private void addStat(JPanel panel, String label, String value) {
        panel.add(this.tag(label));
        panel.add(this.tag(value));
    }

    private JLabel tag(String text) {
        final var tag = new JLabel(text);
        tag.setAlignmentX(CENTER_ALIGNMENT);
        tag.setForeground(WHITE);
        return tag;
    }
}
