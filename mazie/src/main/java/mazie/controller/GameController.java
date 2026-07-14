package mazie.controller;

import java.util.Map;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import mazie.exception.QuitException;
import mazie.exception.RepositoryException;
import mazie.game.GameEngine;
import mazie.model.Artifact;
import mazie.model.Hero;
import mazie.model.monster.Monster;
import mazie.repository.HeroRepository;
import mazie.view.GameView;

public class GameController {

    private GameEngine engine = null;
    private GameView view;
    private final HeroRepository repository;
    private final Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();

    public GameController(GameView view, HeroRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    public GameView getView() {
        return this.view;
    }

    public void setView(GameView view) {
        this.view = view;
    }

    public void start() {

        view.showWelcome();

        Hero hero = null;

        try {
            hero = setup();
            gameLoop(hero);
        } catch (QuitException e) {
            view.showError("You're such a Quitter");
            if (hero == null) {
                return;
            }

            try {
                if (hero.getId() == 0) {
                    repository.save(hero);
                } else {
                    repository.update(hero);
                }
            } catch (RepositoryException ex) {
                System.err.println("[WARNIGN]" + ex.getMessage());
            }
        }
    }

    private Hero setup() {
        try {
            final var heroes = repository.loadAllHeroes();
            final var hero = this.setupHero(this.newGame(heroes), heroes);
            return this.confirmSetup(hero);

        } catch (RepositoryException e) {
            view.showError(e.getMessage());
        }
        return setup();
    }

    private boolean newGame(Map<Integer, Hero> heroes) {
        if (heroes.isEmpty()) {
            return true;
        }
        return view.askNewGame();
    }

    private Hero setupHero(boolean newGame, Map<Integer, Hero> heroes) {
        return newGame ? this.createValidHero() : view.selectHero(heroes);
    }

    private Hero confirmSetup(Hero hero) {
        if (hero == null || !view.confirmHero(hero)) {
            return this.setup();
        }
        System.out.println("the chosen one:"); //#todo remove (debugging)
        System.out.println(hero); //#todo remove (debugging)
        if (hero.getId() == 0) {
            try {
                repository.save(hero);
            } catch (RepositoryException e) {
                view.showError("try again: " + e.getMessage());
                return setup();
            }
        }
        return hero;
    }

    private Hero createValidHero() {
        Hero hero = view.createHero();

        final var violations = validator.validate(hero);
        if (violations.isEmpty()) {
            return hero;
        }

        view.showError(String.join(", ", violations.stream().map(v -> v.getMessage()).toList()));
        return createValidHero();
    }

    private void gameLoop(Hero hero) {

        this.engine = new GameEngine(hero);
        var playing = true;

        view.showStartGame();

        while (playing) {

            System.out.println("current map:\n" + engine.getMapString()); //#todo remove

            playing = turn(hero);

            if (engine.win()) {
                playing = false;
                try {
                    repository.update(hero);
                } catch (RepositoryException e) {
                    view.showError(e.getMessage());
                }
                view.showEndGame(true);
            }
        }
    }

    private boolean turn(Hero hero) {

        final var monster = takeStepOrMonster();
        if (monster == null) {
            return true;
        }

        if (runningAway(monster)) {
            return true;
        }

        final var result = engine.fight();

        // #todo remove debugging string
        System.out.println("fight result\nwin: %s\nlvlup: %s\ndrop: %s".formatted(result.win(), result.levelup(), result.drop()));

        if (result.win()) {
            handleRoundWin(hero, monster, result.damageToHero());
        } else {
            handleRoundLoss(hero);
            return false;
        }

        if (result.levelup()) {
            view.showLevelUp(hero);
        }

        this.handleDrop(result.drop(), hero);
        return true;
    }

    private Monster takeStepOrMonster() {
        final var dir = view.askDirection();
        final var monster = this.engine.move(dir);
        if (monster == null) {
            view.showEmptyStep();
        }
        return monster;
    }

    private boolean runningAway(Monster monster) {
        final boolean feelingAgressive = view.askFightMonster(monster);
        if (feelingAgressive) {
            return false;
        }
        var running = this.engine.runAway();
        view.showRunSuccess(monster, running);
        return running;
    }

    private void handleRoundWin(Hero hero, Monster monster, int damageToHero) {
        view.showFightSummary(damageToHero, hero.getAction(), monster.getAction(), monster.getFinalMessage(), monster.getXpReward());
        try {
            repository.update(hero);
        } catch (RepositoryException e) {
            view.showError(e.getMessage());
        }
    }

    private void handleRoundLoss(Hero hero) {
        view.showEndGame(false);
        try {
            repository.delete(hero);
        } catch (RepositoryException e) {
            view.showError(e.getMessage());
        }
    }

    final void handleDrop(Artifact artifact, Hero hero) {
        if (artifact == null) {
            return;
        }

        if (!view.askKeepArtifact(artifact, hero)) {
            return;
        }

        hero.setArtifact(artifact);
        try {
            repository.update(hero);
        } catch (RepositoryException e) {
            view.showError(e.getMessage());
        }
    }
}
