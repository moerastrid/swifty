package mazie.view;

import java.util.Map;

import mazie.exception.FatalException;
import mazie.exception.SwitchViewException;
import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.monster.Monster;
import mazie.view.gui.GuiView;
import mazie.view.terminal.TerminalView;

public class ViewSwitcher implements GameView {

    private volatile GameView view;

    public ViewSwitcher(GameView initial) {
        this.view = initial;
        this.setListener(initial);
    }

    @Override
    public void close() {
        view.close();
    }

    private void setListener(GameView v) {
        v.setSwitchListener(this::switchView);
    }

    private void switchView() {

        final var newView = (view instanceof TerminalView) ? new GuiView() : new TerminalView();

        this.view = newView;
        this.setListener(newView);
    }

    @Override
    public void setSwitchListener(Runnable switchListener) {
        // internal, not used in ViewSwitcher
        throw new FatalException("not allowed to call setSwitchListener on ViewSwitcher");
    }

    @Override
    public void showError(String error) {
        try {
            view.showError(error);
        } catch (SwitchViewException se) {
            this.showError(error);
        }
    }

    @Override
    public void showWelcome() {
        try {
            view.showWelcome();
        } catch (SwitchViewException se) {
            this.showWelcome();
        }
    }

    @Override
    public boolean askNewGame() {
        try {
            return view.askNewGame();
        } catch (SwitchViewException se) {
            return this.askNewGame();
        }
    }

    @Override
    public Hero createHero() {
        try {
            return view.createHero();
        } catch (SwitchViewException se) {
            return this.createHero();
        }
    }

    @Override
    public Hero selectHero(Map<Integer, Hero> heroes) {
        try {
            return view.selectHero(heroes);
        } catch (SwitchViewException se) {
            return this.selectHero(heroes);
        }
    }

    @Override
    public boolean confirmHero(Hero hero) {
        try {
            return view.confirmHero(hero);
        } catch (SwitchViewException se) {
            return this.confirmHero(hero);
        }
    }

    @Override
    public void showStartGame() {
        try {
            view.showStartGame();
        } catch (SwitchViewException se) {
            this.showStartGame();
        }
    }

    @Override
    public Direction askDirection(Hero hero) {
        try {
            return view.askDirection(hero);
        } catch (SwitchViewException se) {
            return askDirection(hero);
        }
    }

    @Override
    public void showEmptyStep() {
        try {
            view.showEmptyStep();
        } catch (SwitchViewException se) {
            this.showEmptyStep();
        }
    }

    @Override
    public boolean wantToFightMonster(Hero hero, Monster monster) {
        try {
            return view.wantToFightMonster(hero, monster);
        } catch (SwitchViewException se) {
            return wantToFightMonster(hero, monster);
        }
    }

    @Override
    public void showRunSuccess(Monster monster, boolean success) {
        try {
            view.showRunSuccess(monster, success);
        } catch (SwitchViewException se) {
            this.showRunSuccess(monster, success);
        }
    }

    @Override
    public void showEndGame(Hero hero, boolean win) {
        try {
            view.showEndGame(hero, win);
        } catch (SwitchViewException se) {
            this.showEndGame(hero, win);
        }
    }

    @Override
    public void showFightSummary(int damageToHero, Hero hero, Monster monster) {
        try {
            view.showFightSummary(damageToHero, hero, monster);
        } catch (SwitchViewException se) {
            this.showFightSummary(damageToHero, hero, monster);
        }
    }

    @Override
    public void showLevelUp(Hero hero) {
        try {
            view.showLevelUp(hero);
        } catch (SwitchViewException se) {
            this.showLevelUp(hero);
        }
    }

    @Override
    public boolean askKeepArtifact(Artifact artifact, Hero hero) {
        try {
            return view.askKeepArtifact(artifact, hero);
        } catch (SwitchViewException se) {
            return askKeepArtifact(artifact, hero);
        }
    }
}
