package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.monster.Monster;

public class GamePanel extends JPanel {

    private JPanel subPanel;
    private JPanel sidePanel;
    private final MenuBarPanel menuBar;

    public GamePanel() {
        setBorder(BorderFactory.createEmptyBorder());
        setLayout(new BorderLayout());

        menuBar = new MenuBarPanel();
        add(menuBar, BorderLayout.NORTH);
    }

    public void setError(String error) {
        menuBar.setErrorLog(error);
    }

    public void setWelcomePanel(CountDownLatch latch) {
        menuBar.clearLog();
        setSubPanel(new WelcomePanel(latch));
    }

    public void setEndPanel(Hero hero, CountDownLatch latch, boolean win) {
        menuBar.clearLog();
        setHeroPanel(hero);
        setSubPanel(new EndPanel(latch, win));
    }

    public void setEmptyStep() {
        menuBar.setLog("nothing here");
    }

    public void setStartGame() {
        menuBar.setLog("entering the maze...");
    }

    public void setFightSummary(int damageToHero, Hero hero, Monster monster, CountDownLatch latch) {
        setSubPanel(new FightSummaryPanel(monster, hero, latch, damageToHero));
    }

    public void setLevelUp(Hero hero, CountDownLatch latch) {
        menuBar.clearLog();
        setSubPanel(new LevelUpPanel(hero, latch));
    }

    public void setRunSuccess(Monster monster, boolean success) {
        final var text = success ? "You've escaped! %s got nothing on you" : "You can't find the exit of %s, there's no escaping now...";
        menuBar.setLog(text.formatted(monster.getName()));
    }

    public void setDirectionPanel(Hero hero, BlockingQueue<Direction> queue) {
        setHeroPanel(hero);
        setSubPanel(new DirectionPanel(queue));
    }

    public void setArtifactPanel(Artifact artifact, Hero hero, BlockingQueue<Boolean> queue) {
        menuBar.clearLog();
        setHeroPanel(hero);
        setSubPanel(new ArtifactPanel(artifact, hero, queue));
    }

    public void setRunPanel(Hero hero, Monster monster, BlockingQueue<Boolean> queue) {
        menuBar.clearLog();
        setHeroPanel(hero);
        setSubPanel(new RunPanel(monster, hero, queue));
    }

    public void setNewOrLoadGamePanel(BlockingQueue<Boolean> queue) {
        setSubPanel(new NewOrLoadGamePanel(queue));
    }

    public void setNewHeroPanel(BlockingQueue<Hero> queue) {
        setSubPanel(new NewHeroPanel(queue));
    }

    public void setSelectHeroPanel(Map<Integer, Hero> heroes, BlockingQueue<Hero> queue) {
        menuBar.clearLog();
        setSubPanel(new SelectHeroPanel(heroes, queue));
    }

    public void setConfirmPanel(Hero hero, BlockingQueue<Boolean> queue) {
        menuBar.clearLog();
        setSubPanel(new ConfirmPanel(hero, queue));
    }

    public void setSwitchListener(Runnable switchListener) {
        menuBar.setSwitchListener(switchListener);
    }

    private void setSubPanel(JPanel screen) {
        if (subPanel != null) {
            remove(subPanel);
        }
        subPanel = screen;
        add(subPanel, BorderLayout.CENTER);
        setBackground(subPanel.getBackground());
        revalidate();
        repaint();
    }

    private void setHeroPanel(Hero hero) {
        if (sidePanel != null) {
            remove(sidePanel);
        }
        if (hero == null) {
            return;
        }
        final var panel = new JPanel();
        panel.setLayout(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder());
        panel.add(new HeroPanel(hero), BorderLayout.CENTER);
        sidePanel = panel;
        add(sidePanel, BorderLayout.WEST);
    }
}
