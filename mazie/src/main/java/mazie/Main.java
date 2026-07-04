package mazie;

import mazie.controller.GameController;
import mazie.exception.ParseException;
import mazie.repository.SQLiteHeroRepository;
import mazie.view.GameView;
import mazie.view.gui.GuiView;
import mazie.view.terminal.TerminalView;

public class Main {

    public static void main(final String[] args) {
        final var console = parse(args);

        System.out.println("hoi :)");

        GameView view;

        if (console) {
            view = new TerminalView();
        } else {
            view = new GuiView();
        }

        final var repository = new SQLiteHeroRepository();

        var controller = new GameController(view, repository);

        controller.start();
    }

    public static boolean parse(final String[] args) {
        if (args.length == 0) {
            return false;
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
