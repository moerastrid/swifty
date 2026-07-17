package mazie.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.stream.Collectors;
import mazie.exception.QuitException;
import mazie.exception.RepositoryException;
import mazie.game.GameEngine;
import mazie.model.Artifact;
import mazie.model.Hero;
import mazie.model.monster.Monster;
import mazie.repository.HeroRepository;
import mazie.view.GameView;
import mazie.view.ViewSwitcher;

public class GameController {

    private final Validator validator;
    private GameEngine engine = null;
    private final ViewSwitcher switcher;
    private final HeroRepository repository;
    private Hero hero;

    public GameController(Validator validator, GameView view, HeroRepository repository) {
        this.validator = validator;
        this.switcher = new ViewSwitcher(view);
        this.repository = repository;
        this.hero = null;
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

    public void start() {

        switcher.showWelcome();
        hero = null;
        try {
            hero = setup();
            gameLoop(hero);
        } catch (QuitException e) {
            switcher.showError("You're such a Quitter");
            this.close();
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

    private void gameLoop(Hero hero) {

        this.engine = new GameEngine(hero);
        var playing = true;

        switcher.showStartGame();

        while (playing) {

            System.out.println("current map:\n" + engine.getMapString()); //#todo remove

            playing = turn(hero);

            if (engine.win()) {
                playing = false;
                repository.update(hero);
                switcher.showEndGame(hero, true);
            }
        }
    }

    private boolean turn(Hero hero) {

        final var monster = takeStepOrMonster(hero);
        if (monster == null) {
            return true;
        }

        if (runningAway(hero, monster)) {
            return true;
        }

        final var result = engine.fight();

        // #todo remove debugging string
        System.out.printf("fight result\nwin: %s\nlvlup: %s\ndrop: %s%n", result.win(), result.levelUp(), result.drop());

        if (result.win()) {
            handleRoundWin(hero, monster, result.damageToHero());
        } else {
            handleRoundLoss(hero);
            return false;
        }

        if (result.levelUp()) {
            switcher.showLevelUp(hero);
        }

        this.handleDrop(result.drop(), hero);
        return true;
    }

    private Monster takeStepOrMonster(Hero hero) {
        final var dir = switcher.askDirection(hero);
        final var monster = this.engine.move(dir);
        if (monster == null) {
            switcher.showEmptyStep();
        }
        return monster;
    }

    private boolean runningAway(Hero hero, Monster monster) {
        final boolean feelingAggressive = switcher.askFightMonster(hero, monster);
        if (feelingAggressive) {
            return false;
        }
        var running = this.engine.runAway();
        switcher.showRunSuccess(monster, running);
        return running;
    }

    private void handleRoundWin(Hero hero, Monster monster, int damageToHero) {
        switcher.showFightSummary(damageToHero, hero, monster);
        repository.update(hero);
    }

    private void handleRoundLoss(Hero hero) {
        switcher.showEndGame(hero, false);
        repository.delete(hero);
    }

    final void handleDrop(Artifact artifact, Hero hero) {
        if (artifact == null) {
            return;
        }

        if (!switcher.askKeepArtifact(artifact, hero)) {
            return;
        }

        hero.setArtifact(artifact);
        repository.update(hero);
    }
}
