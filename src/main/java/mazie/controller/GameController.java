package mazie.controller;

import mazie.view.GameView;
import mazie.view.console.ConsoleView;

public class GameController {
	private GameView view;

	public GameController() {
		view = new ConsoleView();
	}

	public void startGame() {
		view.startView();
		view.showTitle();
	}
}
