package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mazie.model.Direction;

// gamePanel is persistent. hierin zitten kleinere panels die je kunt afwisselen, geloof ik?
public class GamePanel extends JPanel {

    // private final JTextArea promptTextArea;
    // private final JScrollPane promptScrollPane;
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
        this.add(this.welcomePanel(latch));
        this.revalidate();
        this.repaint();
    }

    public void setDirectionPanel(BlockingQueue queue) {
        this.removeAll();
        this.add(this.directionPanel(queue));
        this.revalidate();
        this.repaint();
    }

    private JPanel welcomePanel(CountDownLatch latch) {
        final var panel = new JPanel();
        panel.setBackground(ThemeColor.GREY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BorderLayout(10, 10));

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

    private JPanel directionPanel(BlockingQueue<Direction> queue) {
        final var panel = new JPanel();
        panel.setBackground(ThemeColor.GREY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BorderLayout(10, 10));

        final var question = new JLabel("where?", JLabel.CENTER);

        panel.add(question, BorderLayout.CENTER);

        final var northButton = new DirectionButton(Direction.NORTH);
        setListener(northButton, queue);
        panel.add(northButton, BorderLayout.NORTH);

        final var eastButton = new DirectionButton(Direction.EAST);
        setListener(eastButton, queue);
        panel.add(eastButton, BorderLayout.EAST);

        final var southButton = new DirectionButton(Direction.SOUTH);
        setListener(southButton, queue);
        panel.add(southButton, BorderLayout.SOUTH);

        final var westButton = new DirectionButton(Direction.WEST);
        setListener(westButton, queue);
        panel.add(westButton, BorderLayout.WEST);

        return panel;
    }

    private void setListener(DirectionButton button, BlockingQueue<Direction> queue) {
        button.addActionListener(event -> {
            try {
                queue.put(button.getDirection());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("gui view ask direction: " + e.getMessage());
            }
        });
    }
}
