package ajav.view.terminal;

import ajav.view.GameView;
import java.util.Scanner;
import ajav.view.terminal.AnsiColor;

public class TerminalGui implements GameView {

	static Scanner in;

	public TerminalGui() {
		in = new Scanner(System.in);
	}

    public void showStart() {
		final var welcome_text = "welcome to mazie, an a-maze-ing RPG";
		System.out.println(AnsiColor.CYAN + welcome_text + AnsiColor.RESET);
    }

    public void showGame() {

    }

    public void showPrompt(String prompt) {
		System.out.println(AnsiColor.PURPLE + prompt + AnsiColor.RESET);
    }

    public void showError(String error) {
		System.out.println(AnsiColor.YELLOW + error + AnsiColor.RESET);
    }

	public String getInput() {
		String s = in.nextLine();
		return s;
	}
}

