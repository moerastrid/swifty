package mazie.view.gui;

import java.awt.GridLayout;
import java.util.concurrent.BlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import mazie.model.Artifact;
import mazie.model.Hero;
import static mazie.view.gui.ThemeColor.GREEN;
import static mazie.view.gui.ThemeColor.GREY;
import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.TEAL;

public class ArtifactPanel extends JPanel {

    public ArtifactPanel(Artifact artifact, Hero hero, BlockingQueue<Boolean> queue) {
        this.setBackground(TEAL);
        this.setForeground(PURPLE);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new GridLayout(4, 1, 20, 20));

        final var note = new JLabel("The monster dropped an artifact!", JLabel.CENTER);
        note.setForeground(GREEN);
        this.add(note);

        final var question = new JTextArea("\nkeep this?\n\n%s".formatted(artifact.toString()));
        question.setBackground(GREY);
        question.setBorder(BorderFactory.createLineBorder(GREEN, 5, true));
        this.add(question);

        final var artifacts = hero.getArtifacts().stream().map(a -> a.toString()).toList();
        final var info = artifacts.isEmpty() ? "nothing" : String.join("\n - ", artifacts);
        final var current = new JTextArea("\nyou're currently wearing:\n\n - %s".formatted(info));
        current.setBackground(GREY);
        current.setBorder(BorderFactory.createLineBorder(GREEN, 5, true));
        this.add(current);

        this.add(new YesNoButtonPanel(queue));
    }
}
