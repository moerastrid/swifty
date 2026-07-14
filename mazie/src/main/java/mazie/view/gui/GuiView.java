package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import static javax.swing.SwingUtilities.invokeLater;

import mazie.exception.QuitException;
import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.monster.Monster;
import mazie.view.GameView;

public class GuiView implements GameView {

    private static final String TITLE = "Mazie - an a-maze-ing RPG";

    private final Thread controllerThread;
    private final JFrame frame;
    private final GamePanel panel;

    public GuiView() {
        this.controllerThread = Thread.currentThread();
        this.panel = new GamePanel();
        this.frame = initFrame();
        this.setIcon();
        this.frame.getContentPane().add(this.panel);

        invokeLater(() -> this.frame.setVisible(true));
    }

    private JFrame initFrame() {
        final var fr = new JFrame(TITLE);

        fr.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        fr.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controllerThread.interrupt();
                fr.dispose();
            }
        });
        fr.setSize(800, 600);
        fr.setLayout(new BorderLayout(10, 10));
        fr.setLocationRelativeTo(null);
        
        return fr;
    }

    private void setIcon() {
        final var url = "/mazie-icon.png";
        final var path = getClass().getResource(url);

        if (path != null) {
            final var icon = new ImageIcon(path);
            this.frame.setIconImage(icon.getImage());
        } else {
            System.err.println("[WAARNING] url[%s] not available?".formatted(url));
        }
    }

    @Override
    public void showError(String error) {
        invokeLater(() -> panel.setError(error));
    }

    @Override
    public void showWelcome() {
        final var latch = new CountDownLatch(1);

        invokeLater(() -> panel.setWelcomePanel(latch));
        try {
            latch.await();
        } catch (InterruptedException e) {
            controllerThread.interrupt();
            throw new QuitException("thread interruption", e);
        }
    }

    @Override
    public boolean askNewGame() {
        final var queue = new SynchronousQueue<Boolean>();

        invokeLater(() -> panel.setNewOrLoadGamePanel(queue));
        try {
            return queue.take();
        } catch (InterruptedException e) {
            controllerThread.interrupt();
            throw new QuitException("thread interruption", e);
        }
    }

    @Override
    public Hero createHero() {
        final var queue = new SynchronousQueue<Hero>();

        invokeLater(() -> panel.setNewHeroPanel(queue));
        try {
            return queue.take();
        } catch (InterruptedException e) {
            controllerThread.interrupt();
            throw new QuitException("thread interruption", e);
        }
    }

    @Override
    public Hero selectHero(Map<Integer, Hero> heroes) {
        final var queue = new SynchronousQueue<Hero>();

        invokeLater(() -> panel.setSelectHeroPanel(heroes, queue));
        try {
            return queue.take();
        } catch (InterruptedException e) {
            controllerThread.interrupt();
            throw new QuitException("thread interruption", e);
        }
    }

    @Override
    public boolean confirmHero(Hero hero) {
        final var queue = new SynchronousQueue<Boolean>();

        invokeLater(() -> panel.setConfirmPanel(hero, queue));
        try {
            return queue.take();
        } catch (InterruptedException e) {
            controllerThread.interrupt();
            throw new QuitException("thread interruption", e);
        }
    }

    @Override
    public void showStartGame() {
        invokeLater(() -> panel.setStartGame());
    }

    @Override
    public Direction askDirection() {
        final var queue = new LinkedBlockingQueue<Direction>();

        invokeLater(() -> panel.setDirectionPanel(queue));
        try {
            return queue.take();
        } catch (InterruptedException e) {
            controllerThread.interrupt();
            throw new QuitException("thread interruption", e);
        }
    }

    @Override
    public void showEmptyStep() {
        invokeLater(() -> panel.setEmptyStep());
    }

    @Override
    public boolean askFightMonster(Monster monster) {
        final var queue = new SynchronousQueue<Boolean>();

        invokeLater(() -> panel.setRunPanel(monster, queue));
        try {
            return queue.take();
        } catch (InterruptedException e) {
            controllerThread.interrupt();
            throw new QuitException("thread interruption", e);
        }
    }

    @Override
    public void showRunSuccess(Monster monster, boolean success) {
        invokeLater(() -> panel.setRunSucces(monster, success));
    }

    @Override
    public void showEndGame(boolean win) {
        final var latch = new CountDownLatch(1);

        invokeLater(() -> panel.setEndPanel(latch, win));
        try {
            latch.await();
        } catch (InterruptedException e) {
            controllerThread.interrupt();
            throw new QuitException("thread interruption", e);
        } finally {
            invokeLater(() -> {
                this.frame.setVisible(false);
                this.frame.dispose();
            });
        }
    }

    // fightsummary has a string with what happened during the fight + how much xp was gained. only called when user won.
    @Override
    public void showFightSummary(int damageToHero, String heroAction, String monsterAction, String finalMessage, int xpGain){
        invokeLater(() -> panel.setFightSummary(damageToHero, heroAction, monsterAction, finalMessage, xpGain));
    }

    @Override
    public void showLevelUp(Hero hero) {
        final var latch = new CountDownLatch(1);
        invokeLater(() -> panel.setLevelUp(hero, latch));
        try {
            latch.await();
        } catch (InterruptedException e) {
            controllerThread.interrupt();
            throw new QuitException("thread interruption", e);
        }
    }

    @Override
    public boolean askKeepArtifact(Artifact artifact, Hero hero) {
        final var queue = new SynchronousQueue<Boolean>();

        invokeLater(() -> panel.setArtifactPanel(artifact, hero, queue));
        try {
            return queue.take();
        } catch (InterruptedException e) {
            controllerThread.interrupt();
            throw new QuitException("thread interruption", e);
        }
    }
}
