package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import static mazie.view.gui.ThemeColor.GREEN;
import static mazie.view.gui.ThemeColor.GREY;
import static mazie.view.gui.ThemeColor.PURPLE;
import static mazie.view.gui.ThemeColor.TEAL;

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

    public void setEndPanel(CountDownLatch latch, boolean win) {
        this.removeAll();
        this.add(this.endPanel(latch, win));
        this.revalidate();
        this.repaint();
    }

    public void setEmptyStep() {
        // #todo notify on banner? or something?
        this.revalidate();
        this.repaint();
    }

    public void setDirectionPanel(BlockingQueue<Direction> queue) {
        this.removeAll();
        this.add(this.directionPanel(queue));
        this.revalidate();
        this.repaint();
    }

    public void setArtifactPanel(Artifact artifact, Hero hero, BlockingQueue<Boolean> queue) {
        this.removeAll();
        this.add(this.artifactPanel(artifact, hero, queue));
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

    private JPanel endPanel(CountDownLatch latch, boolean win) {
        final var panel = new JPanel();
        panel.setBackground(ThemeColor.GREY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BorderLayout(10, 10));

        String text = win ? "you win" : "you lost";
        final var label = new JLabel(text, JLabel.CENTER);

        panel.add(label, BorderLayout.CENTER);

        final var okButton = new JButton("ok");
        okButton.setBackground(ThemeColor.PURPLE);
        okButton.setForeground(ThemeColor.YELLOW);
        okButton.addActionListener(event -> {
            latch.countDown();
        });
        panel.add(okButton, BorderLayout.SOUTH);
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

    private JPanel artifactPanel(Artifact artifact, Hero hero, BlockingQueue<Boolean> queue) {
        final var panel = new JPanel();
        panel.setBackground(TEAL);
        panel.setForeground(PURPLE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridLayout(4, 1, 20, 20));

        final var note = new JLabel("The monster dropped an artifact!", JLabel.CENTER);
        note.setForeground(GREEN);
        panel.add(note);

        final var question = new JTextArea("\nkeep this?\n\n%s".formatted(artifact.toString()));
        question.setBackground(GREY);
        question.setBorder(BorderFactory.createLineBorder(GREEN, 5, true));
        panel.add(question);

        final var artifacts = hero.getArtifacts();
        final var info = artifacts.isEmpty() ? "nothing" : String.join("\n - ", artifacts.toString());
        final var current = new JTextArea("\nyou're currently wearing:\n\n - %s".formatted(info));
        current.setBackground(GREY);
        current.setBorder(BorderFactory.createLineBorder(GREEN, 5, true));
        panel.add(current);

        panel.add(yesNoButtonPanel(queue));

        return panel;
    }

    private JPanel yesNoButtonPanel(BlockingQueue<Boolean> queue) {
        final var panel = new JPanel();

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new FlowLayout());

        final var yesButton = new YesNoButton(true);
        final var noButton = new YesNoButton(false);

        setListener(yesButton, true, queue);
        setListener(noButton, false, queue);

        panel.add(yesButton);
        panel.add(noButton);

        return panel;
    }

    private void setListener(DirectionButton button, BlockingQueue<Direction> queue) {
        setListener(button, button.getDirection(), queue);
    }

    private void setListener(JButton button, Object value, BlockingQueue queue) {
        button.addActionListener(event -> {
            try {
                queue.put(value);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("gui view interrupted: " + e.getMessage());
            }
        });
    }
}
