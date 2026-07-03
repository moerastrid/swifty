package mazie.controller;

import java.util.Collections;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import mazie.exception.QuitException;
import mazie.game.GameEngine;
import mazie.model.Hero;
import mazie.repository.HeroRepository;
import mazie.view.GameView;

public class GameController {

    // private GameEngine engine;
    private GameView view;
    private HeroRepository repository;
    private final Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();

    public GameController(GameView view, HeroRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void start() {

        view.showWelcome();

        try {
            Hero hero = setup();
            gameLoop(hero);
        } catch (QuitException e) {
            view.showError("You're such a Quitter");
        } finally {
            // #todo repository.save of iets dergelijks?
        }

    }

    private Hero setup() {

        // #todo: alleen als er helden zijn om te laten, vragen of user een nieuwe game wil beginnen. Nu geen repo dus altijd true?
        boolean newGame = true;
        Hero hero = null;

        if (!newGame) {
            newGame = view.askNewGame();
        }

        if (!newGame) {
            hero = view.selectHero(Collections.emptyMap());
        }

        if (hero == null) {
            hero = createValidHero();
        }

        view.showHeroStats(hero);

        if (view.confirmHero(hero)) {
            System.out.println("the chosen one:"); //#todo remove (debugging)
            System.out.println(hero); //#todo remove (debugging)
            return hero;
        }
        return setup();
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

        final var engine = new GameEngine(hero);
        var playing = true;

        view.showStartGame();

        while (playing) {
            System.out.println("this is hero:"); //#todo remove
            view.showHeroStats(hero); //#todo removev

            System.out.println("current map:\n" + engine.getMapString()); //#todo remove

            playing = turn(engine, hero);

            if (engine.win()) {
                view.showEndGame(true);
                playing = false;
                // #todo safe hero?
            }

        }
    }

    private boolean turn(GameEngine engine, Hero hero) {
        final var dir = view.askDirection();
        final var monster = engine.move(dir);
        if (monster == null) {
            view.showEmptyStep();
            return true;
        }

        final boolean feelingAgressive = view.askFightMonster(monster);
        var running = false;
        if (!feelingAgressive) {
            running = engine.runAway();
            view.showRunSuccess(monster, running);
        }
        if (running) {
            return true;
        }

        final var result = engine.fight();
        System.out.println("fight result\nwin: %s\nlvlup: %s\ndrop: %s".formatted(result.win(), result.levelup(), result.drop()));

        if (!result.win()) {
            view.showEndGame(false);
            // #todo delete hero?
            return false;
        }

        view.showFightSummary("the fight ended", monster.getXpReward());

        if (result.levelup()) {
            view.showLevelUp(hero);
        }

        if (result.drop() != null) {
            final var keep = view.askKeepArtifact(result.drop(), hero);
            if (keep) {
                hero.setArtifact(result.drop());
            }
        }
        return true;
    }

    public GameView getView() {
        return this.view;
    }

    public void setView(GameView view) {
        this.view = view;
    }
}
