package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.concurrent.BlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mazie.model.Direction;

public class DirectionPanel extends JPanel {

    public DirectionPanel(BlockingQueue<Direction> queue) {
        this.setBackground(ThemeColor.GREY);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout(10, 10));

        final var question = new JLabel("where?", JLabel.CENTER);

        this.add(question, BorderLayout.CENTER);

        final var northButton = new DirectionButton(Direction.NORTH);
        setListener(northButton, queue);
        this.add(northButton, BorderLayout.NORTH);

        final var eastButton = new DirectionButton(Direction.EAST);
        setListener(eastButton, queue);
        this.add(eastButton, BorderLayout.EAST);

        final var southButton = new DirectionButton(Direction.SOUTH);
        setListener(southButton, queue);
        this.add(southButton, BorderLayout.SOUTH);

        final var westButton = new DirectionButton(Direction.WEST);
        setListener(westButton, queue);
        this.add(westButton, BorderLayout.WEST);
    }

    private void setListener(DirectionButton button, BlockingQueue<Direction> queue) {
        button.addActionListener(event -> {
            try {
                queue.put(button.getDirection());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("gui view interrupted: " + e.getMessage());
            }
        });
    }
}
