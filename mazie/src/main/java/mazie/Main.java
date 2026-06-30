package mazie;

import mazie.controller.GameController;
import mazie.exception.ParseException;
import mazie.view.GameView;
import mazie.view.terminal.TerminalView;

public class Main {

    String[] input = {
        "UP", "UP", "DOWN"
    };

    public static void main(final String[] args) {
        final var console = parse(args);

        System.out.println("hoi :)");

        GameView view;

        if (console) {
            view = new TerminalView();
        } else {
            view = new GuiView();
        }

        var controller = new GameController(view);

        controller.start();
    }

    public static boolean parse(final String[] args) {
        if (args.length == 0) {
            return true;
        }

        if (args.length > 1) {
            throw new ParseException("hold up, not so argumentative");
        }

        return switch (args[0].strip().toLowerCase()) {
            case "console", "c" ->
                true;
            case "gui", "g" ->
                false;
            default ->
                throw new ParseException("unknown argument (%s) pls help".formatted(args[0]));
        };
    }
}
