package mazie.view.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import mazie.model.Hero;

import static mazie.view.gui.theme.ThemeColour.LILA;
import static mazie.view.gui.theme.ThemeColour.PURPLE;
import static mazie.view.gui.theme.ThemeColour.WHITE;
import static mazie.view.gui.theme.ThemeFont.HEADER;

public class HeroPanel extends JPanel {

    public HeroPanel(Hero hero) {
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(nameLabel(hero.getName()));
        add(imageLabel(hero));
        add(statsPanel(hero));
        add(artifactTextArea(hero));
    }

    private JLabel nameLabel(String name) {
        final var label = new JLabel(name);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setForeground(PURPLE);
        label.setFont(HEADER);
        label.setPreferredSize(new Dimension(80, 40));
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
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setPreferredSize(new Dimension(80, 100));
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
        return tag;
    }
}
