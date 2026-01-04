package ajav.controller;

import ajav.HeroFactory;
import ajav.exception.FatalException;
import ajav.exception.InvalidInputException;
import ajav.exception.UnexpectedException;
import ajav.model.hero.Hero;
import ajav.view.GameView;

public class GameController {
    private GameView gui;
	private final HeroFactory heroFactory = HeroFactory.getInstance();

	private GameController() {
	}

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
					return this.introduction();
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
		final String prompt = "Awesome! What would you like to do next?\n"
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
					return this.setup();
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

		final var hero = this.createHero();
		gui.showPrompt("Hi there! This is you:\n%s".formatted(hero.toString()));

		return true;
	}

	public boolean loadGame() {
		gui.showPrompt("No games to load yet...");
		this.newGame();
		return true;
	}

	public Hero createHero() {
		String heroType = chooseHeroType();
		String heroName = nameHero();

		return heroFactory.newHero(heroType, heroName);
	}


	public String chooseHeroType() {
		final var prompt = """
			Choose your fighter:
			[PENGUIN]	🐧
			[FROG]		🐸
			[BEAR]		🐻
			[HARE]		🐰
			[TURTLE]	🐢
			""";
		gui.showPrompt(prompt);

		String input = gui.getInput().toUpperCase();

		switch(input) {
			case "PENGUIN", "FROG", "BEAR", "HARE", "TURTLE" -> {
                return input;
            }
			case "Q" -> {
				gui.stop();
				return null;
			}
			default -> {
				gui.showError("Invalid input: " + input);
				// throw new InvalidInputException ??
				return this.chooseHeroType();
			}
		}
	}

	public String nameHero() {
		gui.showPrompt("Name your fighter");

		String input = gui.getInput().toUpperCase();
		if (input == null || input.isEmpty() || input.isBlank())
			return nameHero();
		return input;		
		// String prompt = """
		// 	Are you sure you want to be called [%s]
		// 	[Y] yes
		// 	[N] no
		// 	""".formatted(input);

		// gui.showPrompt(prompt);

		// String conformation = gui.getInput().toUpperCase();
		
		// switch(conformation) {
		// 	case "Y" -> {
        //         return input;
        //     }
		// 	default -> {
		// 		return this.chooseHeroType();
		// 	}
		// }
	}
}