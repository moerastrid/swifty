package ajav.view;

public interface GameView {
    void showStart();

    void showGame();

    void showPrompt(String prompt);

    void showError(String error);
}
