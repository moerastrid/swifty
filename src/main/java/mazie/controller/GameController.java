package mazie.controller;

import mazie.controller.Prompts;
import mazie.exception.FatalException;
import mazie.exception.InvalidInputException;
import mazie.exception.QuitException;
import mazie.model.hero.Hero;
import mazie.view.GameView;

import java.lang.Thread;

public class GameController {
	private GameView view = null;

	public GameController(GameView view) {
		this.view = view;
		view.startView();
	}

	public void startGame() {
		this.setView(view);

		try {
			this.showTitle();
			this.showIntro();
			this.showSetup();
		} catch (QuitException e) {
			view.showError(e.getMessage());
			this.view.stopView();
			System.exit(0);
		}

		// while (true) {
		// 	final String input = view.getInput(); 

		// 	try {
		// 		GameValidator.validate(view, input);
		// 		view.showPrompt(input);
		// 	} catch (InvalidInputException e) {
		// 		view.showError(e.getMessage());
		// 		continue;
		// 	}
		// }
	}

	public void showTitle() {
		view.showTitle();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		view.showPrompt("Ready when you are 👀");
		view.getInput();
	}

	public void showIntro() {
		view.showPrompt(Prompts.INTRO);

		final String input = view.getInput();
		try {
			GameValidator.validate(view, input, Options.INTRO);
		} catch (InvalidInputException e) {
			view.showError(e.getMessage());
			this.showIntro();
		}
	}

	public void showSetup() {
		view.showPrompt(Prompts.SETUP);

		final var input = view.getInput();
		try {
			final var option = GameValidator.validate(view, input, Options.SETUP);
			switch (option) {
				case "N", "NEW" -> this.newGame();
				case "L", "LOAD" -> this.loadGame();
				default -> this.showSetup();
			}
		} catch (InvalidInputException e) {
			view.showError(e.getMessage());
			this.showSetup();
		}
	}

	public void newGame() {
		view.showPrompt(Prompts.NEW_GAME_INFO);

		
	}

	public void loadGame() {
		view.showPrompt(Prompts.NO_LOAD_GAME_INFO);
		this.newGame();
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
