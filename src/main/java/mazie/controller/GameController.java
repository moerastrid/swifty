package mazie.controller;

import mazie.controller.Prompts;
import mazie.exception.FatalException;
import mazie.exception.InvalidInputException;
import mazie.exception.QuitException;
import mazie.model.hero.Hero;
import mazie.model.HeroFactory;
import mazie.view.GameView;

import java.lang.Thread;
import java.util.List;
import java.util.ArrayList;

public class GameController {
	private GameView view = null;
	private HeroFactory heroFactory = HeroFactory.getInstance();

	public GameController(GameView view) {
		this.view = view;
		view.startView();
	}

	public void start() {
		this.setView(view);

		final var hero = this.selectHero();

		System.out.println("yay\n\n this you:%s".formatted(hero.toString()));

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

	public Hero selectHero() {
		try {
			this.showTitle();
			this.showIntro();
			return this.showSetup();
		} catch (QuitException e) {
			view.showError(e.getMessage());
			this.view.stopView();
			System.exit(0);
		}
		return this.selectHero();
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

	public Hero showSetup() {
		view.showPrompt(Prompts.SETUP);

		final var input = view.getInput();
		try {
			final var option = GameValidator.validate(view, input, Options.SETUP);
			return switch (option) {
				case "N", "NEW" -> this.newGame();
				case "L", "LOAD" -> this.loadGame();
				default -> this.showSetup();
			};
		} catch (InvalidInputException e) {
			view.showError(e.getMessage());
			return this.showSetup();
		}
	}

	public Hero newGame() {
		view.showPrompt(Prompts.NEW_GAME_INFO);

		final var hero = this.selectHeroType();
		final var name = this.nameHero();

		hero.setName(name);

		return(hero);
	}

	public Hero selectHeroType() {

		final List<Hero> heroes = new ArrayList<Hero>();
		
		heroes.add(heroFactory.newHero("P", "[PENGUIN]"));
		heroes.add(heroFactory.newHero("F", "[FROG]"));
		heroes.add(heroFactory.newHero("B", "[BEAR]"));
		heroes.add(heroFactory.newHero("H", "[HARE]"));
		heroes.add(heroFactory.newHero("T", "[TURTLE]"));
		
		view.showPrompt(Prompts.SELECT_HERO(heroes));

		try {
			final var input = view.getInput();
			final var validInput = GameValidator.validate(view, input, Options.SELECT_HERO);
			
			return switch (validInput) {
				case "P", "PENGUIN" -> heroes.get(0);
				case "F", "FROG" -> heroes.get(1);
				case "B", "BEAR" -> heroes.get(2);
				case "H", "HARE" -> heroes.get(3);
				case "T", "TURTLE" -> heroes.get(4);
				default -> throw new RuntimeException("??? input should have been valid ???");
			};

		} catch (InvalidInputException e) {
			view.showError(e.getMessage());
			return this.selectHeroType();
		}
	}

	public String nameHero() {
		view.showPrompt(Prompts.NAME_HERO);

		try {
			final var input = view.getInput();
			return GameValidator.validate(view, input);
		} catch (InvalidInputException e) {
			view.showError(e.getMessage());
			return this.nameHero();
		}
	}

	public Hero loadGame() {
		view.showPrompt(Prompts.NO_LOAD_GAME_INFO);
		return this.newGame();
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
