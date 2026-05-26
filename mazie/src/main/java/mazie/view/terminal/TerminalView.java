package mazie.view.terminal;

import java.util.NoSuchElementException;
import java.util.Scanner;

import mazie.view.GameView;


public class TerminalView implements GameView {
    
    private final Scanner in;

    public TerminalView() {
        this.in = new Scanner(System.in);
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
			System.err.println("?user entered ^C or ^D in terminal?\n" + e);
			return null;
		}
	}
}
