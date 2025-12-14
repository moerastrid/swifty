package ajav.view.terminal;

import ajav.view.GameView;
import java.util.Scanner;

public class TerminalGui implements GameView {
	

	static final Scanner in;

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

