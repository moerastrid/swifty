package mazie.bootstrap;

import mazie.controller.GameController;
import mazie.exception.ParseException;
import mazie.repository.HeroRepository;
import mazie.repository.SQLiteHeroRepository;
import mazie.view.GameView;
import mazie.view.gui.GuiView;
import mazie.view.terminal.TerminalView;

public class Application {

    private final GameController controller;
    private final GameView view;
    private final HeroRepository repository;

    private enum ViewType {
        TERMINAL,
        GUI
    };

    public Application(String[] args) {

        this.view = switch (parse(args)) {
            case TERMINAL -> new TerminalView();
            case GUI -> new GuiView();
        };

        this.repository = new SQLiteHeroRepository();

        this.controller = new GameController(view, repository);
    }

    public void start() {
        this.controller.start();
    }

    public void shutDownGracefully() {
        this.controller.close();
    }

    private static ViewType parse(final String[] args) {
        if (args.length == 0) {
            return ViewType.GUI;
        }

        if (args.length > 1) {
            throw new ParseException("hold up, not so argumentative");
        }

        return switch (args[0].strip().toLowerCase()) {
            case "console", "c" -> ViewType.TERMINAL;
            case "gui", "g" -> ViewType.GUI;
            default -> throw new ParseException("unknown argument: [%s]".formatted(args[0]));
        };
    }
}
