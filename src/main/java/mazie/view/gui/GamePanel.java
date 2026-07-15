package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.monster.Monster;

import static mazie.view.gui.ThemeColor.BLACK;
import static mazie.view.gui.ThemeColor.GREY;
import static mazie.view.gui.ThemeColor.WHITE;
import static mazie.view.gui.ThemeColor.YELLOW;

// gamePanel is persistent. Hierin zitten kleinere panels die je kunt afwisselen, geloof ik?
public class GamePanel extends JPanel {

    private JPanel subPanel;
    private JLabel log;

    public GamePanel() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(ThemeColor.LILA);
    }

    public void setError(String error) {
        this.setErrorLog(error);
    }

    public void setWelcomePanel(CountDownLatch latch) {
        this.setSubPanel(new WelcomePanel(latch));
    }

    public void setEndPanel(CountDownLatch latch, boolean win) {
        this.setSubPanel(new EndPanel(latch, win));
    }

    public void setEmptyStep() {
        setLog("nothing here");
    }

    public void setStartGame() {
        setLog("entering the maze...");
    }

    public void setFightSummary(int damageToHero, String heroAction, String monsterAction, String finalMessage, int xpGain) {
        final var fightSummary = "%s (-%d hp). %s. %s (+ %d xp)".formatted(monsterAction, damageToHero, heroAction, finalMessage, xpGain);
        setLog(fightSummary);
    }

    public void setLevelUp(Hero hero, CountDownLatch latch) {
        this.setSubPanel(new LevelUpPanel(hero, latch));
    }

    public void setRunSucces(Monster monster, boolean success) {
        final var text = success ? "You've escaped! That %s got nothing on you" : "You lock eyes with the %s, there's no escaping now...";
        setLog(text.formatted(monster.getName()));
    }

    public void setDirectionPanel(BlockingQueue<Direction> queue) {
        this.setSubPanel(new DirectionPanel(queue));
    }

    public void setArtifactPanel(Artifact artifact, Hero hero, BlockingQueue<Boolean> queue) {
        this.setSubPanel(new ArtifactPanel(artifact, hero, queue));
    }

    public void setRunPanel(Monster monster, BlockingQueue<Boolean> queue) {
        this.clearLog();
        this.setSubPanel(new RunPanel(monster, queue));
    }

    public void setNewOrLoadGamePanel(BlockingQueue<Boolean> queue) {
        this.setSubPanel(new NewOrLoadGamePanel(queue));
    }

    public void setNewHeroPanel(BlockingQueue<Hero> queue) {
        this.setSubPanel(new NewHeroPanel(queue));
    }

    public void setSelectHeroPanel(Map<Integer, Hero> heroes, BlockingQueue<Hero> queue) {
        this.setSubPanel(new SelectHeroPanel(heroes, queue));
    }

    public void setConfirmPanel(Hero hero, BlockingQueue<Boolean> queue) {
        this.clearLog();
        this.setSubPanel(new ConfirmPanel(hero, queue));
    }

    public void setSwitchListener(Runnable switchListener) {
        this.setMenuBar(switchListener);
    }

    private void setMenuBar(Runnable switchListener) {

        JButton switchButton = new JButton("switch view");
        switchButton.setBackground(YELLOW);
        switchButton.setForeground(BLACK);
        switchButton.setMargin(new Insets(2, 10, 2, 10));
        switchButton.addActionListener(event -> switchListener.run());
        JPanel menuBar = new JPanel();
        menuBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        menuBar.setOpaque(true);
        menuBar.add(switchButton);
        this.add(menuBar, BorderLayout.NORTH);
        this.revalidate();
        this.repaint();
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

    private void setLog(String text) {
        if (log != null) {
            this.remove(log);
        }
        log = new JLabel(text, JLabel.CENTER);
        log.setForeground(WHITE);
        log.setBackground(GREY);
        log.setOpaque(true);
        this.add(log, BorderLayout.SOUTH);
        this.revalidate();
        this.repaint();
    }

    private void setErrorLog(String text) {
        if (log != null) {
            this.remove(log);
        }
        log = new JLabel(text, JLabel.CENTER);
        log.setForeground(BLACK);
        log.setBackground(YELLOW);
        log.setOpaque(true);
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
