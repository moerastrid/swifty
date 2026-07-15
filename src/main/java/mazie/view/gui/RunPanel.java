package mazie.view.gui;

import java.awt.GridLayout;
import java.util.concurrent.BlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import mazie.model.monster.Monster;

import static mazie.view.gui.ThemeColor.GREEN;
import static mazie.view.gui.ThemeColor.GREY;
import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.WHITE;
import static mazie.view.gui.ThemeColor.YELLOW;

public class RunPanel extends JPanel {
    public RunPanel(Monster monster, BlockingQueue<Boolean> queue) {
        this.setBackground(PURPLE);
        this.setForeground(WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new GridLayout(4, 1, 20, 20));

        final var note = new JLabel("A wild %s appeared".formatted(monster.getName()), JLabel.CENTER);
        note.setForeground(YELLOW);
        this.add(note);

        final var aboutMonster = new JTextArea(monster.toString());
        aboutMonster.setBackground(GREY);
        aboutMonster.setBorder(BorderFactory.createLineBorder(GREEN, 5, true));
        this.add(aboutMonster);

        final var question = new JTextArea("\nare you bout that action?".formatted(monster.toString()));
        question.setBackground(GREY);
        question.setForeground(PURPLE);
        question.setBorder(BorderFactory.createLineBorder(GREEN, 5, true));
        this.add(question);

        this.add(new YesNoButtonPanel(queue));
    }
}
