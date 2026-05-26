package mazie.controller;

import mazie.game.GameEngine;
import mazie.view.GameView;

public class GameController {
    private GameEngine engine;
    private GameView view;

    public GameController(GameEngine engine, GameView view) {
        this.engine = engine;
        this.view = view;
    }

    public void start() {

        
        
        engine.blabla();


    }

}
