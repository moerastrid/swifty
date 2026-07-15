package mazie.view;

import java.util.Map;
import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.monster.Monster;

public interface GameView {

    // switch listener om view te wisselen
    void setSwitchListener(Runnable switchListener);

    // in case of error (invalid input for example)
    void showError(String error);

    // welcome screen (image or ascii art, depending on view)
    void showWelcome();

    // ask if the user wants to start a new game or load existing one. only prompted if existing heroes are present
    boolean askNewGame();

    // create new hero
    Hero createHero();

    // select existing hero from list (only prompted if list is not empty)
    Hero selectHero(Map<Integer, Hero> heroes);

    // show stats of selected hero, confirm choice
    boolean confirmHero(Hero hero);

    // show that the game is starting
    void showStartGame();

    // get direction player wants to go
    Direction askDirection();

    // if no monster, show user took a step
    void showEmptyStep();

    // show monster, ask if user wants to fight or run
    boolean askFightMonster(Monster monster);

    // show if run attempt from monster was successful
    void showRunSuccess(Monster monster, boolean success);

    // show that the game ended, because either user is at map edge (win) or defeated by a monster (dead)
    void showEndGame(boolean win);

    // fight summary has a string with what happened during the fight + how much xp was gained. only called when user won.
    void showFightSummary(int damageToHero, Hero hero, Monster monster, int xpGain);

    // show hero stats + congratz blabla
    void showLevelUp(Hero hero);

    // show artifact, ask if user wants to keep it, show current hero (+ artifacts) too so user can make choice
    boolean askKeepArtifact(Artifact artifact, Hero hero);

}

/*
todo: hero iig meegeven in:

showStartGame()
askDirection()
showEmptyStep()
askFightMonster(Monster)
showRunSuccess(Monster, boolean)

(als er een hero is)

 */
