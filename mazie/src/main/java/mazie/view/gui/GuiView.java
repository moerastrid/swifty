package mazie.view.gui;

import java.util.Map;

import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.Monster;
import mazie.view.GameView;

public class GuiView implements GameView {

    // in case of error (invalid input for example)
    @Override
    public void showError(String error) {
        //#todo implement
    }

    // welcome screen (image or ascii art, depending on view)
    @Override
    public void showWelcome() {
        // #todo implement
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
        return null;
    }

    // select existing hero from list (only prompted if list is not empty)
    @Override
    public Hero selectHero(Map<Integer, Hero> heroes) {
        // #todo implement
        return null;
    }

    // show stats of selected hero, confirm choice
    @Override
    public boolean confirmHero(Hero hero) {
        // #todo implement
        return false;
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
        return null;
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
