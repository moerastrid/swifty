package mazie.controller;

import mazie.game.GameEngine;
import mazie.model.Hero;
import mazie.repository.HeroRepository;
import mazie.view.GameView;

public class GameSession {

    private final GameView view;
    private final HeroRepository repository;
    private final GameEngine engine;
    private final Hero hero;

    public GameSession(GameView view, HeroRepository repository, Hero hero) {
        this.view = view;
        this.repository = repository;
        this.engine = new GameEngine(hero);
        this.hero = hero;
    }

    public void start() {
        view.showStartGame();

        while (engine.isPlaying()) {
            playTurn();
            repository.update(hero);

            if (engine.heroWins()) {
                view.showEndGame(hero, true);
                return;
            }
            if (engine.heroDies()) {
                view.showEndGame(hero, false);
                repository.delete(hero);
                return;
            }
        }
    }

    public void close() {
        repository.update(hero);
    }

    private void playTurn() {

        final var dir = view.askDirection(hero);
        if (engine.takeStep(dir)) {
            view.showEmptyStep();
            return;
        }

        final var monster = engine.getMonster(dir);
        final var xp = monster.getXpReward();
        final var artifact = monster.getArtifact();

        if (!view.wantToFightMonster(hero, monster)) {
            final var escape = engine.escape();
            view.showRunSuccess(monster, escape);
            if (escape) {
                return;
            }
        }

        final var damageToHero = engine.fight(dir);
        if (engine.heroDies()) {
            return;
        }
        view.showFightSummary(damageToHero, hero, monster);
        engine.takeStep(dir);

        final var levelUp = engine.gainXp(xp);
        if (levelUp) {
            view.showLevelUp(hero);
        }

        if (artifact != null && view.askKeepArtifact(artifact, hero)) {
            hero.setArtifact(artifact);
        }

    }
}
