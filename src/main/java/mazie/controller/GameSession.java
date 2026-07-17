package mazie.controller;

import java.util.Random;
import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.GameMap;
import mazie.model.Hero;
import mazie.model.monster.Monster;
import mazie.repository.HeroRepository;
import mazie.view.GameView;

public class GameSession {

    private final GameView view;
    private final HeroRepository repository;
    private final Hero hero;
    private final GameMap map;
    private final Random random = new Random();

    public GameSession(GameView view, HeroRepository repository, Hero hero) {
        this.view = view;
        this.repository = repository;
        this.hero = hero;
        map = new GameMap(hero.getLevel());
    }

    public Hero getHero() {
        return hero;
    }

    public void start() {
        view.showStartGame();

        while (turn()) {
            System.out.println("current map:\n" + map.toString()); //#todo remove

            if (heroWins()) {
                repository.update(hero);
                view.showEndGame(hero, true);
                return;
            }
        }
    }

    private boolean heroWins() {
        return map.isHeroOnEdge();
    }

    public void close() {
        if (hero.getId() == 0) {
            repository.save(hero);
        } else {
            repository.update(hero);
        }
    }

    // returns true when keep playing, false when game is finito
    private boolean turn() {

        final var dir = view.askDirection(hero);
        final var monster = map.getMonsterInDirection(dir);
        if (monster == null) {
            map.moveHero(dir);
            view.showEmptyStep();
            return true;
        }

        final boolean feelingAggressive = view.askFightMonster(hero, monster);
        if (!feelingAggressive) {
            final var escaped = random.nextBoolean();
            view.showRunSuccess(monster, escaped);
            if (escaped) {
                return true;
            }
        }

        final var damageToHero = fight(monster);

        if (hero.isDead()) {
            view.showEndGame(hero, false);
            repository.delete(hero);
            return false;
        }

        map.removeMonster(dir);
        map.moveHero(dir);
        view.showFightSummary(damageToHero, hero, monster);

        final var lvlUp = hero.gainXp(monster.getXpReward());
        repository.update(hero);

        if (lvlUp) {
            view.showLevelUp(hero);
        }

        handleArtifact(monster);
        return true;
    }

    private int fight(Monster monster) {
        var damageToHero = 0;
        while (!hero.isDead() && !monster.isDead()) {
            damageToHero += fightRound(monster);
        }
        return damageToHero;
    }

    private int fightRound(Monster monster) {
        // hero attacks monster
        monster.takeDamage(hero.getTotalAttack());
        if (monster.isDead()) {
            return 0;
        }

        // monster 50% chance attacks hero
        if (random.nextBoolean()) {
            return 0;
        }
        return hero.takeDamage(monster.getAttack());
    }


    private void handleArtifact(Monster monster) {
        final var value = monster.getArtifactValue();
        final var artifact = Artifact.createArtifact(value);

        if (artifact == null) {
            return;
        }

        if (!view.askKeepArtifact(artifact, hero)) {
            return;
        }

        hero.setArtifact(artifact);
        repository.update(hero);
    }
}
