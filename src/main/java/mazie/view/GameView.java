package mazie.view;

import jakarta.validation.constraints.NotEmpty;

public interface GameView {
	public void startView();

	public void stopView();

	public void showTitle();

	// public void showMenu(); // maybe?

	public void showPrompt(String prompt);

	public void showError(String error);

	@NotEmpty
	public String getInput();
}
