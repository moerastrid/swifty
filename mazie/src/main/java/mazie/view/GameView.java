package mazie.view;

import java.util.Map;

import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.monster.Monster;

public interface GameView {

    // in case of error (invalid input for example)
    public void showError(String error);

    // welcome screen (image or ascii art, depending on view)
    public void showWelcome();

    // ask if the user wants to start a new game or load existing one. only prompted if existing heroes are present
    public boolean askNewGame();

    // create new hero
    public Hero createHero();

    // select existing hero from list (only prompted if list is not empty)
    public Hero selectHero(Map<Integer, Hero> heroes);

    // show stats of selected hero, confirm choice
    public boolean confirmHero(Hero hero);

    // show that the game is starting
    public void showStartGame();

    // get direction player wants to go
    public Direction askDirection();

    // if no monster, show user took a step
    public void showEmptyStep();

    // show monster, ask if user wants to fight or run
    public boolean askFightMonster(Monster monster);

    // show if run attempt from monster was succesfull
    public void showRunSuccess(Monster monster, boolean success);

    // show that the game ended, because either user is at map edge (win) or defeated by a monster (dead)
    public void showEndGame(boolean win);

    // fightsummary has a string with what happened during the fight + how much xp was gained. only called when user won.
    public void showFightSummary(int damageToHero, String heroAction, String monsterAction, String finalMessage, int xpGain);

    // fightsummary has a string with what happened during the fight + how much xp was gained. only called when user won.
    public void showFightSummary(int damageToHero, Hero hero, Monster monster, int xpGain);

    // show hero stats + congratz blabla
    public void showLevelUp(Hero hero);

    // show artifact, ask if user wants to keep it, show current hero (+ artifacts) too so user can make choise
    public boolean askKeepArtifact(Artifact artifact, Hero hero);

}
