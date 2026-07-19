package mazie.view.gui;

import java.awt.GridLayout;
import java.util.concurrent.BlockingQueue;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import mazie.model.Artifact;
import mazie.model.Hero;

import static mazie.view.gui.ThemeColor.GREEN;
import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.TEAL;

public class ArtifactPanel extends JPanel {

    public ArtifactPanel(Artifact artifact, Hero hero, BlockingQueue<Boolean> queue) {
        setBackground(TEAL);
        setForeground(PURPLE);
        setLayout(new GridLayout(4, 1, 20, 20));

        final var note = new JLabel("You found something!", JLabel.CENTER);
        note.setForeground(GREEN);
        add(note);

        final var question = new JTextArea("\nkeep this?\n\n%s".formatted(artifact.toString()));
        question.setEditable(false);
        question.setFocusable(false);
        question.setLineWrap(true);
        question.setWrapStyleWord(true);
        add(question);

        final var artifacts = hero.getArtifacts().stream().map(Artifact::toString).toList();
        final var info = artifacts.isEmpty() ? "nothing" : String.join("\n - ", artifacts);
        final var current = new JTextArea("\nyou're currently wearing:\n\n - %s".formatted(info));
        current.setEditable(false);
        current.setFocusable(false);
        current.setLineWrap(true);
        current.setWrapStyleWord(true);
        add(current);

        add(new YesNoButtonPanel(queue));
    }
}
