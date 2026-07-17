package mazie.controller;

import mazie.game.GameEngine;
import mazie.model.Artifact;
import mazie.model.Hero;
import mazie.model.monster.Monster;
import mazie.repository.HeroRepository;
import mazie.view.GameView;

public class GameSession {

    private final GameView view;
    private final HeroRepository repository;
    private final Hero hero;
    private final GameEngine engine;

    public GameSession(GameView view, HeroRepository repository, Hero hero) {
        this.view = view;
        this.repository = repository;
        this.hero = hero;
        this.engine = new GameEngine(hero);
    }

    public Hero getHero() {
        return this.hero;
    }

    public void start() {
        this.view.showStartGame();

        while (turn()) {
            System.out.println("current map:\n" + this.engine.getMapString()); //#todo remove

            if (this.engine.win()) {
                repository.update(hero);
                this.view.showEndGame(hero, true);
                return;
            }
        }
    }

    public void close() {
        if (this.hero.getId() == 0) {
            repository.save(this.hero);
        } else {
            repository.update(this.hero);
        }
    }

    // returns true when keep playing, false when game is finito
    private boolean turn() {

        final var monster = takeStepOrMonster();
        if (monster == null) {
            return true;
        }

        if (runningAway(monster)) {
            return true;
        }

        final var result = this.engine.fight();

        // #todo remove debugging string
        System.out.printf("fight result\nwin: %s\nlvlup: %s\ndrop: %s%n", result.win(), result.levelUp(), result.drop());

        if (result.win()) {
            handleRoundWin(monster, result.damageToHero());
        } else {
            handleRoundLoss();
            return false;
        }

        if (result.levelUp()) {
            this.view.showLevelUp(hero);
        }

        this.handleDrop(result.drop());
        return true;
    }

    private Monster takeStepOrMonster() {
        final var dir = this.view.askDirection(this.hero);
        final var monster = this.engine.move(dir);
        if (monster == null) {
            this.view.showEmptyStep();
        }
        return monster;
    }

    private boolean runningAway(Monster monster) {
        final boolean feelingAggressive = this.view.askFightMonster(this.hero, monster);
        if (feelingAggressive) {
            return false;
        }
        var running = this.engine.runAway();
        this.view.showRunSuccess(monster, running);
        return running;
    }

    private void handleRoundWin(Monster monster, int damageToHero) {
        this.view.showFightSummary(damageToHero, this.hero, monster);
        repository.update(this.hero);
    }

    private void handleRoundLoss() {
        this.view.showEndGame(this.hero, false);
        repository.delete(this.hero);
    }

    final void handleDrop(Artifact artifact) {
        if (artifact == null) {
            return;
        }

        if (!this.view.askKeepArtifact(artifact, this.hero)) {
            return;
        }

        this.hero.setArtifact(artifact);
        repository.update(this.hero);
    }
}
