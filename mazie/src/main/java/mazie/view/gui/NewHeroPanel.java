package mazie.view.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.concurrent.BlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import mazie.model.Hero;
import mazie.model.HeroType;
import static mazie.view.gui.ThemeColor.GREEN;
import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.TEAL;
import static mazie.view.gui.ThemeColor.WHITE;

public class NewHeroPanel extends JPanel {

    private HeroType type;

    public NewHeroPanel(BlockingQueue<Hero> queue) {
        this.setBackground(TEAL);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new GridLayout(4, 1));

        this.add(new JLabel("create your hero", JLabel.CENTER));

        this.add(typeButtons());

        final var nameField = new JTextField(30);
        this.add(nameField);

        this.add(okButton(nameField, queue));
    }

    private JPanel typeButtons() {
        final var panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(TEAL);

        final var group = new ButtonGroup();

        for (var typeSelect : HeroType.values()) {
            final var button = new JToggleButton(typeSelect.toString().toLowerCase());
            button.setSize(100, 60);
            button.setBackground(GREEN);
            button.setForeground(WHITE);
            
            button.addItemListener(event -> {
                if (button.isSelected()) {
                    this.type = typeSelect;
                    button.setForeground(GREEN);
                } else {
                    button.setForeground(WHITE);
                }
            });
            group.add(button);
            panel.add(button);
        }
        return panel;
    }

    private JButton okButton(JTextField nameField, BlockingQueue<Hero> queue) {
        final var button = new JButton("ok");
        button.setForeground(PURPLE);

        button.addActionListener(event -> {
            try {
                queue.put(new Hero(nameField.getText(), type));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("gui view interrupted: " + e.getMessage());
            }
        });
        return button;
    }

}
