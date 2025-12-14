package ajav.view.terminal;

import ajav.view.GameView;
import java.util.Scanner;

public class TerminalGui implements GameView {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	Scanner in;

	public TerminalGui() {
		in = new Scanner(System.in);
	}

    public void showStart() {
		final var welcome_text = "welcome to mazie, an a-maze-ing RPG";
		System.out.println(ANSI_CYAN + welcome_text + ANSI_RESET);
    }

    public void showGame() {

    }

    public void showPrompt(String prompt) {
		System.out.println(ANSI_PURPLE + prompt + ANSI_RESET);
    }

    public void showError(String error) {
		System.out.println(ANSI_YELLOW + error + ANSI_RESET);
    }

	public String getInput() {
		String s = in.nextLine();
		return s;
	}
}

