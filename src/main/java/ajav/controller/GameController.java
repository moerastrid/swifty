package ajav.controller;

import ajav.exception.FatalErrorException;
import ajav.exception.UnexpectedErrorException;
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
			try {
				String temp = gui.getInput();

				if (temp.equals("Q") || temp.equals("q")) {
					gui.showError("Q is for Quitters");
					break;
				}

				gui.showPrompt(temp);

			} catch (UnexpectedErrorException e) {
				gui.showError(e.getMessage());
				break;
			} catch (FatalErrorException e) {
				break;
			}
		}

		gui.stop();
	}

    // public void getUserInput(String input) {

    // }
}
