package mazie.view;

import java.util.List;

import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.Monster;

public interface GameView {
    // default bedacht:

    public void showPrompt(String prompt);

    public void showError(String error);

    public String getInput();

    // nodig vanuit draw io:

    public void showWelcome();

    public boolean aksNewGame();

    public Hero createHero();

    public Hero selectHero(List<Hero> heroes);

    public boolean confirmHero(Hero hero);

    public void showStartGame();

    public Direction askDirection();

    public void showMonster(Monster monster);

    public boolean askFightMonster();

    public boolean askRunFromMonster();

    


}
