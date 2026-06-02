package mazie.view;

import mazie.model.Direction;

public interface GameView {
    public void showPrompt(String prompt);

    public void showError(String error);

	public String getInput();

    public Direction getDirection();
}
