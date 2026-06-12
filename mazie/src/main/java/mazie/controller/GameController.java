package mazie.controller;

import mazie.game.GameEngine;
import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.GameMap;
import mazie.model.Hero;
import mazie.model.HeroType;
import mazie.view.GameView;

public class GameController {
    // private GameEngine engine;
    private GameView view;

    public GameController(GameView view) {
        this.view = view;
    }

    public void start() {
         

        this.startGamePlay(new Hero("Papa Bear", HeroType.BEAR));

        this.tryOutHeroes();

        // this.tryOutLogic();
    }

    private void startGamePlay(Hero hero) {
        final var engine = new GameEngine(hero);

        System.out.println(hero.toString());

        System.out.println(engine.getMapString());

        var gameLoop = true;

        while (gameLoop) {

            var monster = engine.move(Direction.NORTH);

            // move + get monster
            if (monster != null) {
                System.out.println(monster.toString());

            } else {
                monster = engine.move(Direction.WEST);
                if (monster != null)
                    System.out.println(monster.toString());
                
            }

            // if monster now ?
            if (monster == null) {
                System.out.println("hero moved!");
                System.out.println(engine.getMapString());
                if (engine.win()) {
                    System.out.println("  congrtz you won.  ");
                    gameLoop = false;
                }
            } else {
                // run ?
                System.out.println("Do we fight the monster? NO RUN!");

                if (engine.runAway() == true) {
                    System.out.println("...running...");
                } else {
                    System.out.println("Let's start a fight!");
                    final var result = engine.fight();
                    if (result.win()) {
                        System.out.println("you won the fight");
                        if (result.levelup()) {
                            System.out.println("you level up!");
                        }
                        if (result.drop() != null) {
                            System.out.println("You gain artifact: " + result.drop());
                        }
                    } else {
                        System.out.println("you lost :(");
                        gameLoop = false;
                    }
                }
            }

            // System.out.println(engine.getMapString());


            
        }
        // engine.play();
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
