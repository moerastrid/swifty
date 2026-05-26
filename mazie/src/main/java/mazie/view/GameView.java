package mazie.view;

public interface GameView {
    public void showPrompt(String prompt);

    public void showError(String error);

	public String getInput();
}
