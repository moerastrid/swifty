package mazie.bootstrap;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import mazie.controller.GameController;
import mazie.exception.ParseException;
import mazie.repository.HeroRepository;
import mazie.repository.SQLiteHeroRepository;
import mazie.view.GameView;
import mazie.view.ViewSwitcher;
import mazie.view.gui.GuiView;
import mazie.view.terminal.TerminalView;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

public class Application {

    private final ValidatorFactory validatorFactory;
    private final GameView view;
    private final HeroRepository repository;
    private final GameController controller;

    private enum ViewType {
        TERMINAL,
        GUI
    }

    public Application(String[] args) {

        final var viewType = parse(args);

        validatorFactory = Validation.byDefaultProvider().configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory();

        final var initialView = switch (viewType) {
            case TERMINAL -> new TerminalView();
            case GUI -> new GuiView();
        };

        view = new ViewSwitcher(initialView);
        repository = new SQLiteHeroRepository();
        controller = new GameController(validatorFactory.getValidator(), view, repository);
    }

    public void start() {
        controller.start();
    }

    public void shutDownGracefully() {
        controller.close();
        view.close();
        repository.close();
        validatorFactory.close();
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
