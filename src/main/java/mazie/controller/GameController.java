package mazie.controller;

import mazie.exception.InvalidInputException;
import mazie.view.GameView;

public class GameController {
	private GameView view;
	private GameValidator validator;

	public GameController(GameView view, GameValidator validator) {
		this.view = view;
		this.validator = validator;
	}

	public void startGame() {
		view.startView();
		view.showTitle();
		
		while (true) {
			final String input = view.getInput(); 

			try {
				validator.validate(input);
				view.showPrompt(input);
			} catch (InvalidInputException e) {
				view.showError(e.getMessage());
				continue;
			}
		}
	}

	public void setView(GameView view) {
		if (!this.view.getClass().equals(view.getClass()))
			this.view = view;
	}
}
