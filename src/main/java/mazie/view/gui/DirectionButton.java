package mazie.view.gui;

import javax.swing.JButton;
import mazie.model.Direction;

import static mazie.view.gui.ThemeColor.BLACK;
import static mazie.view.gui.ThemeColor.TEAL;
import static mazie.view.gui.ThemeColor.YELLOW;

public class DirectionButton extends JButton {

    private final Direction dir;

    public DirectionButton(Direction dir) {
        this.dir = dir;
        setText(dir.toString().toLowerCase());
        final var color = switch (dir) {
            case NORTH, SOUTH -> YELLOW;
            case EAST, WEST -> TEAL;
        };
        setBackground(color);
        setForeground(BLACK);
    }

    public Direction getDirection() {
        return dir;
    }
}
