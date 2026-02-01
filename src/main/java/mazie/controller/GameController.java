package mazie.controller;

import mazie.exception.FatalException;
import mazie.exception.InvalidInputException;
import mazie.view.GameView;

public class GameController {
	private GameView view;

	public GameController(GameView view) {
		this.view = view;
	}

	public void startGame() {
		view.startView();
		view.showTitle();
		
		while (true) {
			final String input = view.getInput(); 

			try {
				GameValidator.validate(view, input);
				view.showPrompt(input);
			} catch (InvalidInputException e) {
				view.showError(e.getMessage());
				continue;
			}
		}
	}

	public void setView(GameView newView) {
		if (newView == null)
			throw new FatalException("Error in GameController.setView(): View cannot be null");

		if (this.view == null) {
			this.view = newView;
			this.view.startView();
			return;
		}

		if (this.view == newView || this.view.getClass() == newView.getClass())
			return;

		this.view.stopView();
		this.view = newView;
		this.view.startView();
	}
}
