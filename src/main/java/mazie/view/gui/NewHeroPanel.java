package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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

import static mazie.view.gui.ThemeColor.BLACK;
import static mazie.view.gui.ThemeColor.GREEN;
import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.TEAL;
import static mazie.view.gui.ThemeColor.WHITE;
import static mazie.view.gui.ThemeColor.YELLOW;

public class NewHeroPanel extends JPanel {

    private HeroType type;
    private final JTextField nameField = new JTextField();
    private final JLabel errorLabel = new JLabel("", JLabel.CENTER);

    public NewHeroPanel(BlockingQueue<Hero> queue) {
        this.setBackground(TEAL);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new GridLayout(5, 1));

        errorLabel.setForeground(BLACK);
        this.add(errorLabel);

        this.add(new JLabel("create your hero", JLabel.CENTER));

        this.add(typeButtons());

        this.add(namePanel(nameField));

        this.add(okButton(queue));
    }

    private JPanel namePanel(JTextField nameField) {
        final var panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(TEAL);
        panel.setLayout(new BorderLayout());

        final var nameLabel = new JLabel("give it a name: ", JLabel.CENTER);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(nameLabel, BorderLayout.NORTH);

        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(nameField, BorderLayout.CENTER);

        return panel;
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

    private JButton okButton(BlockingQueue<Hero> queue) {
        final var button = new JButton("ok");
        button.setForeground(PURPLE);
        button.setBackground(WHITE);
        button.setMargin(new Insets(2, 10, 2, 10));

        button.addActionListener(event -> {
            try {
                if (type != null) {
                    queue.put(new Hero(nameField.getText(), type));
                } else {
                    this.errorLabel.setText("select a type");
                    this.errorLabel.setBackground(YELLOW);
                    this.errorLabel.setOpaque(true);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("gui view interrupted: " + e.getMessage());
            }
        });
        return button;
    }
}
