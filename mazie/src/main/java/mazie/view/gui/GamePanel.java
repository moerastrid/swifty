package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import javax.swing.JLabel;
import javax.swing.JPanel;

import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.Monster;

// gamePanel is persistent. hierin zitten kleinere panels die je kunt afwisselen, geloof ik?
public class GamePanel extends JPanel {

    private JPanel subPanel;
    private JLabel log;

    public GamePanel() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(ThemeColor.LILA);
    }

    public void setWelcomePanel(CountDownLatch latch) {
        this.setSubPanel(new WelcomePanel(latch));
    }

    public void setEndPanel(CountDownLatch latch, boolean win) {
        this.setSubPanel(new EndPanel(latch, win));
    }

    public void setEmptyStep() {
        setLog(new JLabel("nothing here", JLabel.CENTER));
    }

    public void setStartGame() {
        setLog(new JLabel("entering the maze...", JLabel.CENTER));
    }

    public void setLevelUp(Hero hero, CountDownLatch latch) {
        this.setSubPanel(new LevelUpPanel(hero, latch));
    }

    public void setRunSucces(Monster monster, boolean success) {
        final var text = success ? "You've escaped! That %s got nothing on you" : "You lock eyes with the %s, there's no escaping now...";
        setLog(new JLabel(text.formatted(monster.getName()), JLabel.CENTER));
    }

    public void setDirectionPanel(BlockingQueue<Direction> queue) {
        this.setSubPanel(new DirectionPanel(queue));
    }

    public void setArtifactPanel(Artifact artifact, Hero hero, BlockingQueue<Boolean> queue) {
        this.clearLog();
        this.setSubPanel(new ArtifactPanel(artifact, hero, queue));
    }

    public void setRunPanel(Monster monster, BlockingQueue<Boolean> queue) {
        this.clearLog();
        this.setSubPanel(new RunPanel(monster, queue));
    }

    private void setSubPanel(JPanel screen) {
        if (subPanel != null) {
            this.remove(subPanel);
        }
        subPanel = screen;
        this.add(screen, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void setLog(JLabel label) {
        if (log != null) {
            this.remove(log);
        }
        log = label;
        this.add(log, BorderLayout.SOUTH);
        this.revalidate();
        this.repaint();
    }

    private void clearLog() {
        if (log != null) {
            this.remove(log);
        }
        this.revalidate();
        this.repaint();
    }
}
