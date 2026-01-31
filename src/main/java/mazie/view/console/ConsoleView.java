package mazie.view.console;

import java.io.Console;

import mazie.view.GameView;

public class ConsoleView implements GameView {
	// static Scanner scanner;
	private Console console;

	public ConsoleView() {}

	@Override
	public void startView() {
		// scanner = new Scanner(System.in);
		console = System.console();
		if(console == null) 
        {
            System.out.print("Error: No console available");
        }
	}

	@Override
	public void stopView() {
		// scanner.close();
	}

	@Override
	public void showTitle() {
		final var welcome_text = "welcome to mazie, an a-maze-ing RPG";
		// System.out.println(AnsiColor.CYAN + welcome_text + AnsiColor.RESET);
		console.printf(AnsiColor.CYAN + welcome_text + AnsiColor.RESET);
	}

	// public void showMenu(); // maybe?

	@Override
    public void showPrompt(String prompt) {
		System.out.println(AnsiColor.PURPLE + prompt + AnsiColor.RESET);
    }

	@Override
    public void showError(String error) {
		System.out.println(AnsiColor.YELLOW + error + AnsiColor.RESET);
    }

	@Override
	public String getInput() {
		return console.readLine();
	}
}