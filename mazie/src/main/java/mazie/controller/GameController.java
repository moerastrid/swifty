package mazie.controller;

import mazie.game.GameEngine;
import mazie.model.Artifact;
import mazie.model.ArtifactType;
import mazie.model.GameMap;
import mazie.model.Hero;
import mazie.model.HeroType;
import mazie.view.GameView;

public class GameController {
    private final GameEngine engine;
    private GameView view;

    public GameController(GameEngine engine, GameView view) {
        this.engine = engine;
        this.view = view;
    }

    public void start() {
        this.tryOutHeroes();
        // this.tryOutLogic();
    }

    public void tryOutHeroes() {
        final var leo = new Hero("Leonardo", HeroType.FROG);
        final var donnie = new Hero("Dontello", HeroType.HARE);
        final var raph = new Hero("Raphael", HeroType.BEAR);
        final var mikey = new Hero("Michaelangelo", HeroType.TURTLE);

        mikey.gainXp(1500);
        mikey.levelUp();

        Artifact hat = new Artifact("a little hat", ArtifactType.HELMET);
        Artifact legging = new Artifact("a glitterlegging", ArtifactType.ARMOUR);
        
        hat.setValue(6);
        donnie.setArtifact(hat);
        hat.setValue(7);
        raph.setArtifact(hat);
        raph.setArtifact(legging);


        System.out.println(leo);
        GameMap leoMap = new GameMap(leo.getLevel());
        System.out.println(leoMap.toString());

        System.out.println(donnie);
        GameMap donnieMap = new GameMap(donnie.getLevel());
        System.out.println(donnieMap.toString());

        System.out.println(raph);
        GameMap raphMap = new GameMap(raph.getLevel());
        System.out.println(raphMap.toString());

        System.out.println(mikey);
        GameMap mikeyMap = new GameMap(mikey.getLevel());
        System.out.println(mikeyMap.toString());

    }

    public GameView getView() {
        return this.view;
    }

    public void setView(GameView view) {
        this.view = view;
    }
}
