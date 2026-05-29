package ajav.view.terminal;

import java.util.NoSuchElementException;
import java.util.Scanner;

import ajav.exception.UnexpectedException;
import ajav.view.GameView;

public class TerminalGui implements GameView {

	static Scanner in;

	public TerminalGui() {
		in = new Scanner(System.in);
	}

	@Override
    public void showStart() {
		final var welcome_text = "welcome to mazie, an a-maze-ing RPG";
		System.out.println(AnsiColor.CYAN + welcome_text + AnsiColor.RESET);
    }

	@Override
    public void showGame() {

    }

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
		try {
			String s = in.nextLine();
			return s;
		} catch (NoSuchElementException e) {
			throw new UnexpectedException("user entered ^C or ^D in terminal?", e);
		}
	}

	@Override
	public void stop() {
		in.close();
	}
}

