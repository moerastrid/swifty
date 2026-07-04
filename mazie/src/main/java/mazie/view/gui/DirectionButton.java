package mazie.view.gui;

import javax.swing.JButton;

import mazie.model.Direction;

public class DirectionButton extends JButton {

    private final Direction dir;

    public DirectionButton(Direction dir) {
        this.dir = dir;
        this.setText(dir.toString().toLowerCase());
        final var color = switch (dir) {
            case NORTH, SOUTH -> ThemeColor.YELLOW;
            case EAST, WEST -> ThemeColor.TEAL;
        };
        this.setBackground(color);
    }

    public Direction getDirection() {
        return dir;
    }
}
