package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import javax.swing.JPanel;
import mazie.model.Direction;

public class DirectionPanel extends JPanel {

    private final static Random RANDOM = new Random();

    public DirectionPanel(BlockingQueue<Direction> queue) {
        setLayout(new GridLayout(3, 3, 100, 100));

        add(empty());
        add(directionPanel(Direction.NORTH, queue));
        add(empty());
        add(directionPanel(Direction.WEST, queue));
        add(empty());
        add(directionPanel(Direction.EAST, queue));
        add(empty());
        add(directionPanel(Direction.SOUTH, queue));
        add(empty());
    }

    private JPanel directionPanel(Direction direction, BlockingQueue<Direction> queue) {
        final var panel = new JPanel();
        panel.setOpaque(false);

        final var button = new DirectionButton(direction);

        button.addActionListener(event -> {
            queue.offer(button.getDirection());
        });
        button.setPreferredSize(new Dimension(80, 30));
        panel.add(button);
        return panel;
    }

    private JPanel empty() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final var screenType = switch (RANDOM.nextInt(5)) {
            case 0 -> PngMap.ScreenType.WHERE;
            default -> PngMap.ScreenType.HUH;
        };
        final var icon = PngMap.getPng(screenType);
        final var image = icon.getImage();

        final var width = (double)getWidth() + 20;
        final var factor = width / icon.getIconWidth();
        final var height = icon.getIconHeight() * factor;
        final var x = (getWidth() - width) / 2;
        final var y = (getHeight() - height) / 2;
        g.drawImage(image, (int) x, (int) y, (int) width, (int) height, this);
    }
}
