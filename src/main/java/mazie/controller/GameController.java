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
    private final ViewSwitcher switcher;
    private final HeroRepository repository;
    private final Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();

    public GameController(GameView view, HeroRepository repository) {
        this.switcher = new ViewSwitcher(view);
        this.repository = repository;
    }

    public void start() {

        switcher.showWelcome();

        Hero hero = null;

        try {
            hero = setup();
            gameLoop(hero);
        } catch (QuitException e) {
            switcher.showError("You're such a Quitter");
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
        return newGame ? this.createValidHero() : switcher.selectHero(heroes);
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

    private Hero createValidHero() {
        Hero hero = switcher.createHero();

        final var violations = validator.validate(hero);
        if (violations.isEmpty()) {
            return hero;
        }

        switcher.showError(String.join(", ", violations.stream().map(v -> v.getMessage()).toList()));
        return createValidHero();
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
                try {
                    repository.update(hero);
                } catch (RepositoryException e) {
                    switcher.showError(e.getMessage());
                }
                switcher.showEndGame(true);
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
            switcher.showLevelUp(hero);
        }

        this.handleDrop(result.drop(), hero);
        return true;
    }

    private Monster takeStepOrMonster() {
        final var dir = switcher.askDirection();
        final var monster = this.engine.move(dir);
        if (monster == null) {
            switcher.showEmptyStep();
        }
        return monster;
    }

    private boolean runningAway(Monster monster) {
        final boolean feelingAgressive = switcher.askFightMonster(monster);
        if (feelingAgressive) {
            return false;
        }
        var running = this.engine.runAway();
        switcher.showRunSuccess(monster, running);
        return running;
    }

    private void handleRoundWin(Hero hero, Monster monster, int damageToHero) {
        switcher.showFightSummary(damageToHero, hero, monster, monster.getXpReward());
        try {
            repository.update(hero);
        } catch (RepositoryException e) {
            switcher.showError(e.getMessage());
        }
    }

    private void handleRoundLoss(Hero hero) {
        switcher.showEndGame(false);
        try {
            repository.delete(hero);
        } catch (RepositoryException e) {
            switcher.showError(e.getMessage());
        }
    }

    final void handleDrop(Artifact artifact, Hero hero) {
        if (artifact == null) {
            return;
        }

        if (!switcher.askKeepArtifact(artifact, hero)) {
            return;
        }

        hero.setArtifact(artifact);
        try {
            repository.update(hero);
        } catch (RepositoryException e) {
            switcher.showError(e.getMessage());
        }
    }
}
