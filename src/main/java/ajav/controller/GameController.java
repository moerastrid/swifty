package ajav.controller;

import ajav.view.GameView;

public class GameController {
    private GameView gui;

	private GameController() {}

	public GameController(GameView gui) {
		this.gui = gui;
	}

	public void startGame() {
		gui.showStart();
		

		gui.showPrompt("press any key to start, or Q to quit");
		while (true) {
			String temp = gui.getInput();

			if (temp.equals("Q") || temp.equals("q"))
				break;

			gui.showPrompt(temp);
		}
		gui.showError("game over");

		gui.stop();
	}

    // public void getUserInput(String input) {

    // }
}
