package ajav.swing;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.Insets;
import java.util.Locale;

import static javax.swing.SwingConstants.CENTER;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class SwingGui {
    private static final String TITLE = "Mazie - an a-maze-ing RPG";
    private final JFrame frame = initFrame();

    private Font fontHeader;
    private Font fontInput;
    private Font fontText;

    private JPanel startPanel;
    private JPanel gamePanel;

    private JTextField textField;

    private JLabel promptLabel;
    private JLabel errorLabel;

    public SwingGui() {
        init();

        frame.add(startPanel, BorderLayout.CENTER);

//        frame.add(gamePanel, BorderLayout.CENTER);
    }

    private void init() {
        initFonts();

        promptLabel = initPromptLabel();
        errorLabel = initErrorLabel();

        startPanel = initStartPanel();
        gamePanel = initGamePanel();
    }

    private void initFonts() {

        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        String[] fonts;
        if (osName.contains("win")) {
            fonts = new String[] {"Cambria Math", "Lucidat Console", "NSimSun"};
        } else if (osName.contains("linux")) {
            fonts = new String[] {"DejaVu Math TeX Gyre", "DialogInput", "Dialog"};
        } else {
            fonts = new String[] {"Serif", "SansSerif", "SansSerif"};
        }

        fontHeader = new Font(fonts[0], Font.BOLD, 24);
        fontInput = new Font(fonts[1], Font.PLAIN, 14);
        fontText = new Font(fonts[2], Font.PLAIN, 16);
    }

    private JFrame initFrame() {
        var newFrame = new JFrame(TITLE);
        newFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        newFrame.setSize(800, 600);
        newFrame.setLayout(new BorderLayout(10, 10));
        newFrame.setLocationRelativeTo(null);

//        newFrame.setFont(fontHeader); -> doet niks
        newFrame.setBackground(ThemeColor.BLACK); //-> doet niks
//        newFrame.setForeground(ThemeColor.LILA); -> doet niks

        final var icon = new ImageIcon("src/main/resources/mazie-icon.png");
        newFrame.setIconImage(icon.getImage());

        return newFrame;
    }

    private JPanel initStartPanel() {
        var panel = new JPanel();
        panel.setBackground(ThemeColor.GREY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BorderLayout(10, 10));

        final var imageLabel = initImageLabel();
        panel.add(imageLabel, BorderLayout.CENTER);

        final var startButton = initStartButton();
        panel.add(startButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel initGamePanel() {
        var panel = new JPanel();
        panel.setBackground(ThemeColor.TEAL);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridLayout(4, 1));

        final var titleLabel = initTitleLabel();
        panel.add(titleLabel);

        panel.add(promptLabel);

        textField = createTextField();
        panel.add(textField);

        panel.add(errorLabel);

        return panel;
    }

    private JLabel initTitleLabel() {
        var label = new JLabel(TITLE);
        label.setFont(fontHeader);
        label.setHorizontalAlignment(CENTER);
        label.setForeground(ThemeColor.PURPLE);
        return label;
    }

    private JLabel initPromptLabel() {
        var label = new JLabel("the prompt from the game");
        label.setFont(fontText);
        label.setHorizontalAlignment(CENTER);
        label.setForeground(ThemeColor.BLACK);
        return label;
    }

    private JLabel initErrorLabel() {
        var label = new JLabel("");
        label.setFont(fontText);
        label.setHorizontalAlignment(CENTER);
        label.setForeground(ThemeColor.PURPLE);
        return label;
    }

    private JLabel initImageLabel() {
        final var image = new ImageIcon("src/main/resources/mazie-logo.png");
        var label = new JLabel(image);
        label.setFont(fontText);
        label.setHorizontalAlignment(CENTER);
        label.setForeground(ThemeColor.PURPLE);
        return label;
    }

    private Button initStartButton() {
        var button = new Button("Start");
        button.setBackground(ThemeColor.PURPLE);
        button.setFont(fontInput);
        button.setForeground(ThemeColor.YELLOW);
        button.addActionListener(e -> {
            frame.remove(startPanel);
            frame.add(gamePanel, BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        });
        return button;
    }

    public void show() {
        frame.setVisible(true);
    }

    private JTextField createTextField() {
        JTextField newTextField = new JTextField(20);
        newTextField.setFont(fontInput);
        newTextField.setForeground(ThemeColor.GREEN);
        newTextField.setBackground(ThemeColor.YELLOW);
        newTextField.setMargin(new Insets(5, 10, 5, 10));
        newTextField.setBorder(BorderFactory.createLoweredBevelBorder());

        newTextField.addActionListener(event -> errorLabel.setText(textField.getText()));

        return newTextField;
    }
}
