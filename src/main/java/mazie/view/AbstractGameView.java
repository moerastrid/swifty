package mazie.view;

public abstract class AbstractGameView implements GameView {
    protected final Thread mainThread;

    protected AbstractGameView(Thread mainThread) {
        this.mainThread = mainThread;
    }
}
