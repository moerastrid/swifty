package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import mazie.exception.FatalException;
import mazie.exception.QuitException;
import mazie.exception.SwitchViewException;
import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.monster.Monster;
import mazie.view.AbstractGameView;
import mazie.view.gui.theme.Theme;

import static javax.swing.SwingUtilities.invokeAndWait;
import static javax.swing.SwingUtilities.invokeLater;

public class GuiView extends AbstractGameView {

    private static final String TITLE = "Mazie - an a-maze-ing RPG";
    private JFrame frame;
    private GamePanel panel;
    private volatile boolean switchRequested = false;

    public GuiView(Thread mainThread) {
        super(mainThread);
        try {
            invokeAndWait(() -> initGUI());
        } catch (InterruptedException e) {
            handleInterruption(e);
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            throw new FatalException("Cannot start gui view", e.getCause());
        }
    }

    private void initGUI() {
        Theme.configure();
        frame = new JFrame(TITLE);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainThread.interrupt();
                frame.dispose();
            }
        });
        frame.setIconImage(PngMap.getPng(PngMap.ScreenType.ICON).getImage());
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        panel = new GamePanel();
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    @Override
    public void close() {
        if (SwingUtilities.isEventDispatchThread()) {
            closeFrame();
        } else {
            invokeLater(this::closeFrame);
        }
    }

    private void closeFrame() {
        if (this.frame != null) {
            this.frame.setVisible(false);
            this.frame.dispose();
        }
    }

    private void handleInterruption(InterruptedException e) {
        if (this.switchRequested) {
            this.switchRequested = false;
            invokeLater(this::closeFrame);
            throw new SwitchViewException();
        }
        Thread.currentThread().interrupt();
        throw new QuitException("thread interruption", e);
    }

    @Override
    public void setSwitchListener(Runnable switchListener) {
        invokeLater(() -> panel.setSwitchListener(() -> {
            this.switchRequested = true;
            switchListener.run();
            mainThread.interrupt();
        }));
    }

    @Override
    public void showError(String error) {
        invokeLater(() -> panel.setError(error));
    }

    @Override
    public void showStartGame() {
        invokeLater(panel::setStartGame);
    }

    @Override
    public void showEmptyStep() {
        invokeLater(panel::setEmptyStep);
    }

    @Override
    public void showRunSuccess(Monster monster, boolean success) {
        invokeLater(() -> panel.setRunSuccess(monster, success));
    }

    @Override
    public void showWelcome() {
        showBlockingPanel(panel::setWelcomePanel);
    }

    @Override
    public void showFightSummary(int damageToHero, Hero hero, Monster monster) {
        showBlockingPanel(latch -> panel.setFightSummary(damageToHero, hero, monster, latch));
    }

    @Override
    public void showLevelUp(Hero hero) {
        showBlockingPanel(latch -> panel.setLevelUp(hero, latch));
    }

    @Override
    public void showEndGame(Hero hero, boolean win) {
        try {
            showBlockingPanel(latch -> panel.setEndPanel(hero, latch, win));
        } finally {
            invokeLater(this::close);
        }
    }

    @Override
    public boolean askNewGame() {
        return askUser(panel::setNewOrLoadGamePanel);
    }

    @Override
    public Hero createHero() {
        return askUser(panel::setNewHeroPanel);
    }

    @Override
    public Hero selectHero(Map<Integer, Hero> heroes) {
        return askUser(queue -> panel.setSelectHeroPanel(heroes, queue));
    }

    @Override
    public boolean confirmHero(Hero hero) {
        return askUser(queue -> panel.setConfirmPanel(hero, queue));
    }

    @Override
    public Direction askDirection(Hero hero) {
        return askUser(queue -> panel.setDirectionPanel(hero, queue));
    }

    @Override
    public boolean wantToFightMonster(Hero hero, Monster monster) {
        return askUser(queue -> panel.setRunPanel(hero, monster, queue));
    }

    @Override
    public boolean askKeepArtifact(Artifact artifact, Hero hero) {
        return askUser(queue -> panel.setArtifactPanel(artifact, hero, queue));
    }

    private void showBlockingPanel(Consumer<CountDownLatch> consumer) {
        final var latch = new CountDownLatch(1);
        invokeLater(() -> consumer.accept(latch));
        try {
            latch.await();
        } catch (InterruptedException e) {
            handleInterruption(e);
            throw new AssertionError(e);
        }
    }

    private <T> T askUser(Consumer<BlockingQueue<T>> consumer) {
        final var queue = new SynchronousQueue<T>();
        invokeLater(() -> consumer.accept(queue));
        try {
            return queue.take();
        } catch (InterruptedException e) {
            handleInterruption(e);
            throw new AssertionError(e);
        }
    }
}
