package ajav.view;

public interface GameView {
    public void showStart();

    public void showGame();

    public void showPrompt(String prompt);

    public void showError(String error);

	public String getInput();
}
