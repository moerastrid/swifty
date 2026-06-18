package mazie.view.terminal;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.HeroType;
import mazie.model.Monster;
import mazie.view.GameView;


public class TerminalView implements GameView {
    
    private final Scanner scanner;

    public TerminalView() {
        this.scanner = new Scanner(System.in);
    }

	@Override
    public void showError(String error) {
		colorPrint(AnsiColor.YELLOW, error);
    }

	@Override
	public void showWelcome() {
		try (var inputstream = getClass().getResourceAsStream("/mazie-icon-ascii.txt")) {
			String content = new String(inputstream.readAllBytes());
			System.out.print(content);
		} catch (IOException ex) {
			showError(ex.getMessage());
		}
	}

	@Override
	public boolean askNewGame() {
		final var prompt = "Do you want to start a new game? (y/n)";
		final var invalidPrompt = "not a valid choise, try again.";

		colorPrint(AnsiColor.CYAN, prompt);

		final var answer = scanNextLine();
		if (answer == null)
			return askNewGame();

		return switch (answer.toLowerCase()) {
			case "y", "yes" -> true;
			case "n", "no" -> false;
			default -> {
				showError(invalidPrompt + "\n" + prompt);
				yield askNewGame();
			}
		};
	}

	@Override
	public Hero createHero() {
		final var typePrompt = """
		What do you want to be?
			- bear (b)
			- frog (f)
			- hare (h)
		""";
		
		HeroType type = null;
		while (type == null) {
			colorPrint(AnsiColor.PURPLE, typePrompt);

			final var input = scanNextLine();
			if (input == null) {
				showError("that's not my type, try again");
			} else {
				switch (input.toLowerCase()) {
					case "b", "bear" -> type = HeroType.BEAR;
					case "f", "frog" -> type = HeroType.FROG;
					case "h", "hare" -> type = HeroType.HARE;
					default -> showError("that's not my type, try again");
				}
			}
		}

		final var namePrompt = "Name your %s: ".formatted(type);
		String name = "";

		while (name.isBlank()) {
			colorPrint(AnsiColor.BLUE, namePrompt);
			
			final var input = scanNextLine();
			if (input != null) {
				name = input;
			} else {
				showError("that's not a proper name?!");
			}
		}

		return new Hero(name, type);
	}

	@Override
	public Hero selectHero(List<Hero> heroes) {
		//#todo implement
		return null;
	}

	@Override
	public boolean confirmHero(Hero hero) {
		//#todo implement
		return false;
	}

	@Override
	public void showHeroStats(Hero hero) {
		//#todo implement
	}

	@Override
	public void showStartGame() {
		//#todo implement
	}

	@Override
	public Direction askDirection() {
		//#todo implement
		return null;
	}

	@Override
	public boolean askFightMonster(Monster monster) {
		//#todo implement
		return false;
	}

	@Override
	public void showRunSuccess(Monster monster, boolean success) {
		//#todo implement
	}

	@Override
	public void showEndGame(boolean win) {
		//#todo implement
	}

	@Override
	public void showFightSummary(String fightSummary, int xpGained) {
		//#todo implement
	}

	@Override
	public void showLevelUp(Hero hero) {
		//#todo implement
	}

	@Override
	public boolean askKeepArtifact(Artifact artifact, Hero hero) {
		//#todo implement
		return false;
	}

	private void colorPrint(AnsiColor color, String text) {
		System.out.println(color + text + AnsiColor.RESET);
	}

	private String scanNextLine() {
		try {
			String s = scanner.nextLine();
			return s;
		} catch (NoSuchElementException e) {
			showError("?user entered ^C or ^D in terminal?" + e);
			return null;
		}
	}


	// @Override
    // public void showPrompt(String prompt) {
	// 	System.out.println(AnsiColor.PURPLE + prompt + AnsiColor.RESET);
    // }

	// @Override
	// public String getInput() {
	// 	try {
	// 		String s = in.nextLine();
	// 		return s;
	// 	} catch (NoSuchElementException e) {
	// 		System.err.println("?user entered ^C or ^D in terminal?\n" + e);
	// 		return null;
	// 	}
	// }

	// @Override
	// public Direction getDirection() {
	// 	// #ToDo: implement.
	// 	return Direction.SOUTH;
	// }
}
