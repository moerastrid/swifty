package mazie.controller;

import mazie.view.GameView;
import mazie.view.console.ConsoleView;

public class GameController {
	private static GameView view;

	public GameController() {
		view = new ConsoleView();
	}

	public static void startGame() {
		view.startView();
		view.showTitle();
	}
}
