package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

// gamePanel is persistent. hierin zitten kleinere panels die je kunt afwisselen, geloof ik?
public class GamePanel extends JPanel {

    // private final JTextArea promptTextArea;
    // private final JScrollPane promptScrollPane;
    // private final JButton northButton;
    // private final JButton eastButton;
    // private final JButton southButton;
    // private final JButton westButton;
    // private final JOptionPane directionOptionPane;
    // private final GridLayout directionGridLayout;

    // private final JLabel label;
    // private final JList list;
    // private final JTextPane textPane;

    public GamePanel() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(ThemeColor.LILA);

        
        // promptTextArea = new JTextArea();
        // promptTextArea.setEditable(false);
        // promptScrollPane = new JScrollPane();
        // promptScrollPane.add(promptTextArea);

        // northButton = new JButton();
        // eastButton = new JButton();
        // southButton = new JButton();
        // westButton = new JButton();

        // directionOptionPane = new JOptionPane();
        // directionGridLayout = new GridLayout(1, 4);
        // directionOptionPane.setLayout(directionGridLayout);
        // directionOptionPane.add(northButton);
        // directionOptionPane.add(eastButton);
        // directionOptionPane.add(southButton);
        // directionOptionPane.add(westButton);

        // this.add(promptScrollPane);
        // this.add(directionOptionPane);


        // label = new JLabel();
        // list = new JList();
        // textPane = new JTextPane();
    }

    public void setWelcomePanel(CountDownLatch latch) {
        this.removeAll();
        this.add(welcomePanel(latch));
        this.revalidate();
        this.repaint();
    }

    

    private JPanel welcomePanel(CountDownLatch latch) {
        final var panel = new JPanel();
        panel.setBackground(ThemeColor.GREY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BorderLayout(10,10));
        
        final var image = new ImageIcon(getClass().getResource("/mazie-logo.png"));
        final var imageLabel = new JLabel(image);
        imageLabel.setForeground(ThemeColor.PURPLE);
        panel.add(imageLabel, BorderLayout.CENTER);

        final var startButton = new JButton("Start");
        startButton.setBackground(ThemeColor.PURPLE);
        startButton.setForeground(ThemeColor.YELLOW);
        startButton.addActionListener(event -> {
            latch.countDown();
        });
        panel.add(startButton, BorderLayout.SOUTH);
        return panel;
    }
}
