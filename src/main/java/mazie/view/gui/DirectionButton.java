package mazie.view.gui;

import java.awt.Insets;
import javax.swing.JButton;
import mazie.model.Direction;

import static mazie.view.gui.ThemeColor.TEAL;
import static mazie.view.gui.ThemeColor.YELLOW;

public class DirectionButton extends JButton {

    private final Direction dir;

    public DirectionButton(Direction dir) {
        this.dir = dir;
        this.setText(dir.toString().toLowerCase());
        this.setMargin(new Insets(2, 10, 2, 10));
        final var color = switch (dir) {
            case NORTH, SOUTH -> YELLOW;
            case EAST, WEST -> TEAL;
        };
        this.setBackground(color);
    }

    public Direction getDirection() {
        return dir;
    }
}
