package mazie.view;

public interface GameView {
	public void startView();

	public void stopView();

	public void showTitle();

	// public void showMenu(); // maybe?

	public void showPrompt(String prompt);

	public void showError(String error);

	public String getInput();
}
