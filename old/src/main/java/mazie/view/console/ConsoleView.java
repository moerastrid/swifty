package mazie.view.console;

import java.io.Console;

import mazie.exception.FatalException;
import mazie.view.GameView;

public class ConsoleView implements GameView {
	private Console console;

	public ConsoleView() {}

	@Override
	public void startView() {
		console = System.console();
		if(console == null) 
            throw new FatalException("Error: No console available");
	}

	@Override
	public void stopView() {
		console = null;
	}

	@Override
	public void showTitle() {
		final var welcome_text = "welcome to mazie, an a-maze-ing RPG\n";
		console.printf(AnsiColor.CYAN + welcome_text + AnsiColor.RESET + "\n");
	}

	public void showGoodbye() {
		final var goodbye_text = "goodbye from mazie, an a-maze-ing RPG\n";
		console.printf(AnsiColor.CYAN + goodbye_text + AnsiColor.RESET + "\n");
	}

	// public void showMenu(); // maybe?

	@Override
    public void showPrompt(String prompt) {
		console.printf(AnsiColor.PURPLE + prompt + AnsiColor.RESET + "\n");
    }

	@Override
    public void showError(String error) {
		console.printf(AnsiColor.YELLOW + error + AnsiColor.RESET + "\n");
    }

	@Override
	public String getInput() {
		return console.readLine();
	}
}
