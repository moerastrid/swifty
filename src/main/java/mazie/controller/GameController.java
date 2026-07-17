package mazie.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.stream.Collectors;
import mazie.exception.QuitException;
import mazie.exception.RepositoryException;
import mazie.model.Hero;
import mazie.repository.HeroRepository;
import mazie.view.GameView;
import mazie.view.ViewSwitcher;

public class GameController {

    private final Validator validator;
    private final ViewSwitcher switcher;
    private final HeroRepository repository;
    private Hero hero;

    public GameController(Validator validator, GameView view, HeroRepository repository) {
        this.validator = validator;
        this.switcher = new ViewSwitcher(view);
        this.repository = repository;
        this.hero = null;
    }

    public void start() {
        switcher.showWelcome();
        hero = null;
        try {
            hero = setup();
            final var gameSession = new GameSession(switcher, repository, hero);
            gameSession.start();
        } catch (QuitException e) {
            switcher.showError("You're such a Quitter");
            this.close();
        }
    }

    public void close() {
        if (hero == null) {
            return;
        }
        if (hero.getId() == 0) {
            repository.save(hero);
        } else {
            repository.update(hero);
        }
    }

    private Hero setup() {
        try {
            final var heroes = repository.loadAllHeroes();
            final var hero = this.setupHero(this.newGame(heroes), heroes);
            return this.confirmSetup(hero);

        } catch (RepositoryException e) {
            switcher.showError(e.getMessage());
        }
        return setup();
    }

    private boolean newGame(Map<Integer, Hero> heroes) {
        if (heroes.isEmpty()) {
            return true;
        }
        return switcher.askNewGame();
    }

    private Hero setupHero(boolean newGame, Map<Integer, Hero> heroes) {
        return newGame ? this.createHero() : switcher.selectHero(heroes);
    }

    private Hero confirmSetup(Hero hero) {
        if (hero == null || !switcher.confirmHero(hero)) {
            return this.setup();
        }
        System.out.println("the chosen one:"); //#todo remove (debugging)
        System.out.println(hero); //#todo remove (debugging)
        if (hero.getId() == 0) {
            try {
                repository.save(hero);
            } catch (RepositoryException e) {
                switcher.showError("try again: " + e.getMessage());
                return setup();
            }
        }
        return hero;
    }

    private Hero createHero() {
        Hero hero = switcher.createHero();

        final var violations = validator.validate(hero);
        if (violations.isEmpty()) {
            return hero;
        }
        final var errorMessage = "Hero not valid: %s".formatted(
                violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "))
        );

        switcher.showError(errorMessage);

        return this.createHero();
    }
}
