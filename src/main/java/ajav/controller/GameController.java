package ajav.controller;

import ajav.exception.FatalException;
import ajav.exception.UnexpectedException;
import ajav.exception.InvalidInputException;
import ajav.view.GameView;

public class GameController {
    private GameView gui;

	private GameController() {}

	public GameController(GameView gui) {
		this.gui = gui;
	}

	public void startGame() {

		

		gui.showStart();

		if (!introduction() || !setup()) {
			gui.stop();
			return;
		}

		while (true) {
			try {
				String temp = gui.getInput();

				if (temp.equals("Q") || temp.equals("q")) {
					gui.showError("Q is for Quitters");
					break;
				}

				gui.showPrompt(temp);

			} catch (InvalidInputException e) {
				gui.showError(e.getMessage());
				break;
			} catch (UnexpectedException e) {
				gui.showError(e.getMessage());
				break;
			} catch (FatalException e) {
				break;
			}
		}

		gui.stop();
	}

	public boolean introduction() {
		final var prompt = "Welcome to Mazie!\n"
				+ " - An a-maze-ing RPG -\n\n"
				+ "To play the game, please enter commands in the console.\n"
				+ "Possible commands will be shown in the prompt, between brackets [HERE].\n"
				+ "For example, to move up, enter the command 'up', shown as [UP].\n"
				+ "You can always quit the game by entering 'Q' or 'q'.\n\n"
				+ "Let's get started and try your first command!\n"
				+ "[C] continue\n"
				+ "[Q] quit.\n";
		gui.showPrompt(prompt);

		try {
			String temp = gui.getInput().toUpperCase();

			switch(temp) {
				case "Q":
					gui.showError("Q is for Quitters");
					return false;
				case "C":
					return true;
				default:
					gui.showError("Invalid input: " + temp);
					// throw new InvalidInputException ??
					return introduction();
			}
		} catch (InvalidInputException e) {
			gui.showError(e.getMessage());
			return introduction();
		} catch (UnexpectedException e) {
			gui.showError(e.getMessage());
			return introduction();
		} catch (FatalException e) {
			return false;
		}
	}

    public boolean setup() { 
		final var prompt = "Awesome! What would you like to do next?\n"
			+ "[N] start a New game\n"
			+ "[L] Load a saved game\n";
		gui.showPrompt(prompt);

				try {
			String temp = gui.getInput().toUpperCase();

			switch(temp) {
				case "Q":
					gui.showError("Q is for Quitters");
					return false;
				case "N":
					return this.newGame();
				case "L":
					return this.loadGame();

				default:
					gui.showError("Invalid input: " + temp);
					// throw new InvalidInputException ??
					return this.introduction();
			}
		} catch (InvalidInputException e) {
			gui.showError(e.getMessage());
			return this.introduction();
		} catch (UnexpectedException e) {
			gui.showError(e.getMessage());
			return this.introduction();
		} catch (FatalException e) {
			return false;
		}
    }

	public boolean newGame() {
		gui.showPrompt("starting a new game...");
		return true;
	}

	public boolean loadGame() {
		gui.showPrompt("No games to load yet...");
		this.newGame();
		return true;
	}
}
