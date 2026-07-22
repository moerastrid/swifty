package mazie.view.gui;

import java.awt.Dimension;
import java.util.concurrent.BlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import mazie.model.Hero;
import mazie.model.HeroType;
import mazie.view.gui.theme.Theme;

import static mazie.view.gui.theme.ThemeColour.BLACK;
import static mazie.view.gui.theme.ThemeColour.GREEN;
import static mazie.view.gui.theme.ThemeColour.LILA;
import static mazie.view.gui.theme.ThemeColour.PURPLE;
import static mazie.view.gui.theme.ThemeColour.TEAL;
import static mazie.view.gui.theme.ThemeColour.YELLOW;
import static mazie.view.gui.theme.ThemeFont.HEADER;

public class NewHeroPanel extends JPanel {

    private final JTextField nameField;
    private final JLabel errorLabel;
    private HeroType type;

    public NewHeroPanel(BlockingQueue<Hero> queue) {
        this.setBackground(TEAL);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(title());
        this.add(typeButtons());

        nameField = createNameField();
        this.add(nameField);

        errorLabel = createErrorLabel();
        this.add(errorLabel);

        this.add(okButton(queue));
    }

    private JLabel title() {
        final var label = new JLabel("create your hero", JLabel.CENTER);
        label.setForeground(BLACK);
        label.setFont(HEADER);
        label.setAlignmentX(CENTER_ALIGNMENT);
        return label;
    }

    private JTextField createNameField() {
        final var field = new JTextField();
        field.setBorder(BorderFactory.createTitledBorder(Theme.TEXT_AREA_BORDER, "give it a name:"));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setMaximumSize(new Dimension(300, 100));
        return field;
    }

    private JPanel typeButtons() {
        final var panel = new JPanel();
        panel.setBackground(TEAL);

        final var group = new ButtonGroup();

        for (var typeSelect : HeroType.values()) {

            final var typeTitle = typeSelect.toString().toLowerCase();
            final var typeIcon = PngMap.getButtonIcon(typeSelect);
            final var button = new JToggleButton(typeTitle, typeIcon);
            button.setSize(100, 60);
            button.addItemListener(event -> {
                if (button.isSelected()) {
                    this.type = typeSelect;
                    button.setForeground(GREEN);
                } else {
                    button.setForeground(LILA);
                }
            });
            group.add(button);
            panel.add(button);
        }

        return panel;
    }

    private JLabel createErrorLabel() {
        final var label = new JLabel("", JLabel.CENTER);
        label.setForeground(BLACK);
        label.setBackground(YELLOW);
        label.setOpaque(false);
        return label;
    }

    private JButton okButton(BlockingQueue<Hero> queue) {
        final var button = new JButton("ok");
        button.setForeground(PURPLE);
        button.setBackground(YELLOW);

        button.addActionListener(event -> {
            if (type != null) {
                queue.offer(new Hero(nameField.getText(), type));
            } else {
                errorLabel.setText("select a type");
                errorLabel.setOpaque(true);
            }
        });
        return button;
    }
}
