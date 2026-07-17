package mazie.controller;

import jakarta.validation.Validator;
import mazie.exception.QuitException;
import mazie.repository.HeroRepository;
import mazie.view.GameView;

public class GameController {

    private final GameView view;
    private final HeroRepository repository;
    private final GameSetupService setupService;
    private GameSession session;

    public GameController(Validator validator, GameView view, HeroRepository repository) {
        this.view = view;
        this.repository = repository;
        this.setupService = new GameSetupService(validator, view, repository);
    }

    public void start() {
        view.showWelcome();
        try {
            final var hero = setupService.setupHero();
            this.session = new GameSession(view, repository, hero);
            this.session.start();
        } catch (QuitException e) {
            view.showError("You're such a Quitter");
            this.close();
        }
    }

    public void close() {
        if (this.session != null) {
            this.session.close();
        }
    }
}
