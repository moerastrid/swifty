package mazie.view.gui;

import java.awt.Font;
import java.util.concurrent.BlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mazie.model.Hero;
import mazie.model.monster.Monster;

import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.WHITE;
import static mazie.view.gui.ThemeColor.YELLOW;

public class RunPanel extends JPanel {
    public RunPanel(Monster monster, Hero hero, BlockingQueue<Boolean> queue) {
        this.setBackground(PURPLE);
        this.setForeground(WHITE);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(note(monster));
        this.add(about(monster));
        this.add(new FightScenePanel(monster, hero));
        this.add(question());
        final var buttonPanel = new YesNoButtonPanel(queue);
        buttonPanel.setBackground(PURPLE);
        this.add(buttonPanel);
    }

    private JLabel note(Monster monster) {
        final var label = new JLabel("You enter %s".formatted(monster.getName()));
        label.setForeground(YELLOW);
        label.setAlignmentX(CENTER_ALIGNMENT);
        final var bigFont = label.getFont().deriveFont(Font.BOLD, 24f);
        label.setFont(bigFont);
        return label;
    }

    private JLabel about(Monster monster) {
        final var info = "attack: %d      defence: %d      hp: %d".formatted(monster.getAttack(), monster.getDefence(), monster.getHp());
        final var label = new JLabel(info);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        return label;
    }

    private JLabel question() {
        final var label = new JLabel("are you willing to stay?");
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        return label;
    }
}
