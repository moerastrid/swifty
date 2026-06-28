package mazie.controller;

import java.util.Collections;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import mazie.exception.QuitException;
import mazie.model.Artifact;
import mazie.model.GameMap;
import mazie.model.Hero;
import mazie.model.HeroType;
import mazie.view.GameView;

public class GameController {
    // private GameEngine engine;
    private GameView view;
    private final Validator validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory()
                .getValidator();

    public GameController(GameView view) {
        this.view = view;
    }

    public void start() {

        view.showWelcome();

        try {
            Hero hero = setup();
            
            gameLoop();
        } catch(QuitException e) {
            view.showError("You're such a Quitter");
        } finally {
            // #todo repository.save of iets dergelijks?
        }
        

        // this.startGamePlay(new Hero("Papa Bear", HeroType.BEAR));

        // this.tryOutHeroes();

        // this.tryOutLogic();
    }

    private Hero setup() {
        
        // #todo: alleen als er helden zijn om te laten, vragen of user een nieuwe game wil beginnen. Nu geen repo dus altijd true?
        boolean newGame = true;
        Hero hero = null;

        if (!newGame) {
            newGame = view.askNewGame();
        }

        if (!newGame) {
            hero = view.selectHero(Collections.emptyList());
        } 

        if (hero == null) {
            hero = createValidHero();
        }

        view.showHeroStats(hero);

        if (view.confirmHero(hero) == true) {
            System.out.println("the chosen one:");
            System.out.println(hero);
            return hero;
        } else {
            return setup();
        }
    }

    private Hero createValidHero() {
        Hero hero = view.createHero();
        
        final var violations = validator.validate(hero);
        if (violations.isEmpty()) {
            return hero;
        }
        
        view.showError(String.join(", ", violations.stream().map(v -> v.getMessage()).toList()));
        return createValidHero();
    }

    private void gameLoop() {
        // #todo implement
    }





    public void tryOutHeroes() {
        final var leo = new Hero("Leonardo", HeroType.FROG);
        final var donnie = new Hero("Dontello", HeroType.HARE);
        final var raph = new Hero("Raphael", HeroType.BEAR);

        if (leo.gainXp(1500))
            System.out.println("lvl up!");

        Artifact helmet = Artifact.helmet(6);
        Artifact armour = Artifact.armour(7);

        donnie.setArtifact(helmet);
        raph.setArtifact(helmet);
        raph.setArtifact(armour);

        System.out.println(leo);
        GameMap leoMap = new GameMap(leo.getLevel());
        System.out.println(leoMap.toString());

        System.out.println(donnie);
        GameMap donnieMap = new GameMap(donnie.getLevel());
        System.out.println(donnieMap.toString());

        System.out.println(raph);
        GameMap raphMap = new GameMap(raph.getLevel());
        System.out.println(raphMap.toString());

        
    }

    public void tryOutLogic(Hero hero) {

    }

    public GameView getView() {
        return this.view;
    }

    public void setView(GameView view) {
        this.view = view;
    }
}
