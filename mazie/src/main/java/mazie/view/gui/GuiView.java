package mazie.view.gui;

import java.awt.BorderLayout;
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
import mazie.model.HeroType;
import mazie.model.Monster;
import mazie.view.GameView;

public class GuiView implements GameView {

    private static final String TITLE = "Mazie - an a-maze-ing RPG";

    private final JFrame frame;
    private final GamePanel panel;

    public GuiView() {
        this.panel = new GamePanel();
        this.frame = initFrame();
        this.frame.getContentPane().add(this.panel);

        invokeLater(() -> this.frame.setVisible(true));
    }

    private JFrame initFrame() {
        final var fr = new JFrame(TITLE);
        final var path = getClass().getResource("/mazie-icon.png");
        final var icon = new ImageIcon(path);

        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setSize(800, 600);
        fr.setLayout(new BorderLayout(10, 10));
        fr.setLocationRelativeTo(null);
        fr.setIconImage(icon.getImage());
        return fr;
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
            Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
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
    public void showFightSummary(String fightSummary, int xpGained) {
        invokeLater(() -> panel.setFightSummary(fightSummary, xpGained));
    }

    @Override
    public void showLevelUp(Hero hero) {
        final var latch = new CountDownLatch(1);
        invokeLater(() -> panel.setLevelUp(hero, latch));
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
            throw new QuitException("thread interruption", e);
        }
    }
}
