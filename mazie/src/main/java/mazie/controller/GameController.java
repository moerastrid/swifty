package mazie.controller;

import java.util.Collections;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import mazie.exception.QuitException;
import mazie.game.GameEngine;
import mazie.model.Artifact;
import mazie.model.GameMap;
import mazie.model.Hero;
import mazie.model.HeroType;
import mazie.view.GameView;

public class GameController {

    // private GameEngine engine;
    private GameView view;
    private final Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();

    public GameController(GameView view) {
        this.view = view;
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

        // this.startGamePlay(new Hero("Papa Bear", HeroType.BEAR));
        // this.tryOutHeroes();
        // this.tryOutLogic();
    }

    private Hero setup() {

        // #todo: alleen als er helden zijn om te laten, vragen of user een nieuwe game wil beginnen. Nu geen repo dus altijd true?
        boolean newGame = true;
        Hero hero = null;

        if (!newGame) {
            newGame = view.askNewGame();
        }

        if (!newGame) {
            hero = view.selectHero(Collections.emptyList());
        }

        if (hero == null) {
            hero = createValidHero();
        }

        view.showHeroStats(hero);

        if (view.confirmHero(hero) == true) {
            System.out.println("the chosen one:");
            System.out.println(hero);
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

            System.out.println("current map:\n" + engine.getMapString());

            playing = turn(engine, hero);

            if (engine.win()) {
                view.showEndGame(true);
                playing = false;
                // #todo safe hero?
            }

            System.out.println("this is hero:");
            view.showHeroStats(hero);
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

    public void tryOutHeroes() {
        final var leo = new Hero("Leonardo", HeroType.FROG);
        final var donnie = new Hero("Dontello", HeroType.HARE);
        final var raph = new Hero("Raphael", HeroType.BEAR);

        if (leo.gainXp(1500)) {
            System.out.println("lvl up!");
        }

        Artifact helmet = Artifact.helmet(6);
        Artifact armour = Artifact.armour(7);

        donnie.setArtifact(helmet);
        raph.setArtifact(helmet);
        raph.setArtifact(armour);

        System.out.println(leo);
        GameMap leoMap = new GameMap(leo.getLevel());
        System.out.println(leoMap.toString());

        System.out.println(donnie);
        GameMap donnieMap = new GameMap(donnie.getLevel());
        System.out.println(donnieMap.toString());

        System.out.println(raph);
        GameMap raphMap = new GameMap(raph.getLevel());
        System.out.println(raphMap.toString());

    }

    public void tryOutLogic(Hero hero) {

    }

    public GameView getView() {
        return this.view;
    }

    public void setView(GameView view) {
        this.view = view;
    }
}
