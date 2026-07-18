package mazie.controller;

import mazie.game.GameEngine;
import mazie.model.Hero;
import mazie.model.monster.Monster;
import mazie.repository.HeroRepository;
import mazie.view.GameView;

public record GameSession(GameView view, HeroRepository repository, GameEngine engine, Hero hero) {

    public GameSession(GameView view, HeroRepository repository, Hero hero) {
        this(view, repository, new GameEngine(hero), hero);
    }

    public void start() {
        view.showStartGame();

        while (!engine.gameOver()) {
            playTurn();
        }
        endGame();
    }

    public void close() {
        if (hero.isDead()) {
            repository.delete(hero);
        } else {
            repository.update(hero);
        }
    }

    private void endGame() {
        if (engine.heroWins()) {
            view.showEndGame(hero, true);
        }
        if (engine.heroDies()) {
            view.showEndGame(hero, false);
        }
    }

    private void playTurn() {

        final var dir = view.askDirection(hero);
        if (engine.takeStep(dir)) {
            view.showEmptyStep();
            return;
        }

        final var monster = engine.getMonster(dir);

        if (!view.wantToFightMonster(hero, monster)) {
            final var escape = engine.escape();
            view.showRunSuccess(monster, escape);
            if (escape) {
                return;
            }
        }

        if (handleFight(monster)) {
            engine.takeStep(dir);
            repository.update(hero);
        } else {
            repository.delete(hero);
        }
    }

    private boolean handleFight(Monster monster) {
        final var xp = monster.getXpReward();
        final var artifact = monster.getArtifact();

        final var damageToHero = engine.fight(monster);
        if (engine.heroDies()) {
            return false;
        }
        view.showFightSummary(damageToHero, hero, monster);

        final var levelUp = engine.gainXp(xp);
        if (levelUp) {
            view.showLevelUp(hero);
        }

        if (artifact != null && view.askKeepArtifact(artifact, hero)) {
            hero.setArtifact(artifact);
        }
        return true;
    }
}
