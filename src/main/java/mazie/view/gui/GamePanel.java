package mazie.view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.monster.Monster;

import static mazie.view.gui.ThemeColor.BLACK;
import static mazie.view.gui.ThemeColor.GREEN;
import static mazie.view.gui.ThemeColor.GREY;
import static mazie.view.gui.ThemeColor.WHITE;
import static mazie.view.gui.ThemeColor.YELLOW;

// gamePanel is persistent. Hierin zitten kleinere panels die je kunt afwisselen, geloof ik?
public class GamePanel extends JPanel {

    private JPanel subPanel;
    private JPanel sidePanel;
    private JLabel log;

    public GamePanel() {
        setDefaultLog("", GREY, GREY);
        setBorder(BorderFactory.createEmptyBorder());
        setLayout(new BorderLayout());
    }

    public void setError(String error) {
        setErrorLog(error);
    }

    public void setWelcomePanel(CountDownLatch latch) {
        setDefaultLog("", GREY, GREY);
        setSubPanel(new WelcomePanel(latch));
    }

    public void setEndPanel(Hero hero, CountDownLatch latch, boolean win) {
        setDefaultLog("", GREY, GREY);
        setHeroPanel(hero);
        setSubPanel(new EndPanel(latch, win));
    }

    public void setEmptyStep() {
        setLog("nothing here");
    }

    public void setStartGame() {
        setLog("entering the maze...");
    }

    public void setFightSummary(int damageToHero, Hero hero, Monster monster, CountDownLatch latch) {
        setSubPanel(new FightSummaryPanel(monster, hero, latch, damageToHero));
    }

    public void setLevelUp(Hero hero, CountDownLatch latch) {
        setDefaultLog("", GREY, GREY);
        setSubPanel(new LevelUpPanel(hero, latch));
    }

    public void setRunSuccess(Monster monster, boolean success) {
        final var text = success ? "You've escaped! %s got nothing on you" : "You can't find the exit of %s, there's no escaping now...";
        setLog(text.formatted(monster.getName()));
    }

    public void setDirectionPanel(Hero hero, BlockingQueue<Direction> queue) {
        setHeroPanel(hero);
        setSubPanel(new DirectionPanel(queue));
    }

    public void setArtifactPanel(Artifact artifact, Hero hero, BlockingQueue<Boolean> queue) {
        setDefaultLog("", GREY, GREY);
        setHeroPanel(hero);
        setSubPanel(new ArtifactPanel(artifact, hero, queue));
    }

    public void setRunPanel(Hero hero, Monster monster, BlockingQueue<Boolean> queue) {
        setDefaultLog("", GREY, GREY);
        setHeroPanel(hero);
        setSubPanel(new RunPanel(monster, hero, queue));
    }

    public void setNewOrLoadGamePanel(BlockingQueue<Boolean> queue) {
        setDefaultLog("", GREY, GREY);
        setSubPanel(new NewOrLoadGamePanel(queue));
    }

    public void setNewHeroPanel(BlockingQueue<Hero> queue) {
        setDefaultLog("", GREY, GREY);
        setSubPanel(new NewHeroPanel(queue));
    }

    public void setSelectHeroPanel(Map<Integer, Hero> heroes, BlockingQueue<Hero> queue) {
        setDefaultLog("", GREY, GREY);
        setSubPanel(new SelectHeroPanel(heroes, queue));
    }

    public void setConfirmPanel(Hero hero, BlockingQueue<Boolean> queue) {
        setDefaultLog("", GREY, GREY);
        setSubPanel(new ConfirmPanel(hero, queue));
    }

    public void setSwitchListener(Runnable switchListener) {
        add(new MenuBarPanel(switchListener), BorderLayout.NORTH);
        revalidate();
        repaint();
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

    private void setLog(String text) {
        setDefaultLog(text, WHITE, GREEN);
    }

    private void setErrorLog(String text) {
        setDefaultLog(text, BLACK, YELLOW);
    }

    private void setDefaultLog(String text, Color foreground, Color background) {
        if (log != null) {
            remove(log);
        }
        log = new JLabel(text, JLabel.CENTER);
        log.setForeground(foreground);
        log.setBackground(background);
        log.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        log.setOpaque(true);
        add(log, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    private void clearLog() {
        if (log != null) {
            setDefaultLog("", GREY, GREY);
            log.setOpaque(false);
        }
        revalidate();
        repaint();
    }
}
