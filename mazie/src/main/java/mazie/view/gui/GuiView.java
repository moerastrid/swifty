package mazie.view.gui;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.HeroType;
import mazie.model.Monster;
import mazie.view.GameView;

public class GuiView implements GameView {

    private static final String TITLE = "Mazie - an a-maze-ing RPG";

    private JFrame frame;
    private GamePanel panel;

    public GuiView() {
        panel = new GamePanel();
        frame = initFrame();
        frame.getContentPane().add(panel);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
            }
        });
    }

    private JFrame initFrame() {
        final var fr = new JFrame(TITLE);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setSize(800, 600);
        fr.setLayout(new BorderLayout(10, 10));
        fr.setLocationRelativeTo(null);
        final var path = getClass().getResource("/mazie-icon.png");
        final var icon = new ImageIcon(path);

        fr.setIconImage(icon.getImage());
        return fr;
    }

    // in case of error (invalid input for example)
    @Override
    public void showError(String error) {

        //#todo implement
    }

    @Override
    public void showWelcome() {
        final var latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            panel.setWelcomePanel(latch);
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("gui view welcome: " + e.getMessage());
        }
    }

    // ask if the user wants to start a new game or load existing one. only prompted if existing heroes are present
    @Override
    public boolean askNewGame() {
        // #todo implement
        return false;
    }

    // create new hero
    @Override
    public Hero createHero() {
        // #todo implement
        return new Hero("hare5", HeroType.HARE);
    }

    // select existing hero from list (only prompted if list is not empty)
    @Override
    public Hero selectHero(Map<Integer, Hero> heroes) {
        // #todo implement
        return new Hero(9, "hare5", HeroType.HARE, 1, 220, 15, 6, 78);
    }

    // show stats of selected hero, confirm choice
    @Override
    public boolean confirmHero(Hero hero) {
        // #todo implement
        return Boolean.TRUE;
    }

    // show stats of selected hero
    @Override
    public void showHeroStats(Hero hero) {
        // #todo implement
    }

    // show that the game is starting
    @Override
    public void showStartGame() {
        // #todo implement
    }

    // get direction player wants to go
    @Override
    public Direction askDirection() {
        // #todo implement
        return Direction.EAST;
    }

    // if no monster, show user took a step
    @Override
    public void showEmptyStep() {
        // #todo implement
    }

    // show monster, ask if user wants to fight or run
    @Override
    public boolean askFightMonster(Monster monster) {
        // #todo implement
        return false;
    }

    // show if run attempt from monster was succesfull
    @Override
    public void showRunSuccess(Monster monster, boolean success) {
        // #todo implement
    }

    // show that the game ended, because either user is at map edge (win) or defeated by a monster (dead)
    @Override
    public void showEndGame(boolean win) {
        // #todo implement
    }

    // fightsummary has a string with what happened during the fight + how much xp was gained. only called when user won.
    @Override
    public void showFightSummary(String fightSummary, int xpGained) {
        // #todo implement
    }

    // show hero stats + congratz blabla
    @Override
    public void showLevelUp(Hero hero) {
        // #todo implement
    }

    // show artifact, ask if user wants to keep it, show current hero (+ artifacts) too so user can make choise
    @Override
    public boolean askKeepArtifact(Artifact artifact, Hero hero) {
        // #todo implement
        return false;
    }

}
