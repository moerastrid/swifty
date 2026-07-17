package mazie.bootstrap;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
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
    private final Validator validator;
    private final GameController controller;
    private final GameView view;
    private final HeroRepository repository;

    private enum ViewType {
        TERMINAL,
        GUI
    };

    public Application(String[] args) {
        this.validatorFactory = Validation.byDefaultProvider().configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory();
        this.validator = validatorFactory.getValidator();

        final var initialView = switch (parse(args)) {
            case TERMINAL -> new TerminalView();
            case GUI -> new GuiView();
        };
        this.view = new ViewSwitcher(initialView);

        this.repository = new SQLiteHeroRepository();

        this.controller = new GameController(validator, view, repository);
    }

    public void start() {
        this.controller.start();
    }

    public void shutDownGracefully() {
        this.validatorFactory.close();
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
