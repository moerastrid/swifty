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
        this.engine = new GameEngine(view, hero);
        this.hero = hero;
    }

    public void start() {
        view.showStartGame();

        while (engine.isPlaying()) {
            engine.playTurn();
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
}
