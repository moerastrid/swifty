package mazie.view.gui;

import java.awt.Dimension;
import java.util.concurrent.BlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

import static mazie.view.gui.theme.ThemeColor.GREEN;
import static mazie.view.gui.theme.ThemeColor.PURPLE;
import static mazie.view.gui.theme.ThemeColor.YELLOW;

public class YesNoButtonPanel extends JPanel {
    private static final Dimension DI = new Dimension(100, 35);
    private static final Border LINE = BorderFactory.createLineBorder(YELLOW, 2, true);

    public YesNoButtonPanel(BlockingQueue<Boolean> queue) {
        this.add(yes(queue));
        this.add(no(queue));
    }

    private JButton yes(BlockingQueue<Boolean> queue) {
        final var button = new JButton("yes");
        button.setPreferredSize(DI);
        button.setBorder(LINE);
        button.setBackground(PURPLE);
        button.addActionListener(event -> queue.offer(true));
        return button;
    }

    private JButton no(BlockingQueue<Boolean> queue) {
        final var button = new JButton("no");
        button.setPreferredSize(DI);
        button.setBorder(LINE);
        button.setBackground(GREEN);
        button.addActionListener(event -> queue.offer(false));
        return button;
    }
}
