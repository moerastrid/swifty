package mazie.game;

import java.util.Random;

import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.GameMap;
import mazie.model.Hero;
import mazie.model.monster.Monster;

public class GameEngine {

    private final Hero hero;
    private final GameMap map;
    private final Random random = new Random();
    private Direction currentDir = null;

    public GameEngine(Hero hero) {
        this.hero = hero;
        this.map = new GameMap(hero.getLevel());
    }

    public String getMapString() {
        return this.map.toString();
    }

    public Monster move(Direction dir) {
        this.currentDir = dir;

        final var monster = map.getMonsterInDirection(dir);

        if (monster != null) {
            return monster;
        }

        map.moveHero(dir);
        this.currentDir = null;
        return null;
    }

    public boolean runAway() {
        final var escaped = this.random.nextBoolean();
        if (escaped) {
            currentDir = null;
        }
        return escaped;
    }

    public boolean win() {
        return this.map.isHeroOnEdge();
    }

    public FightResult fight() {
        final var monster = this.map.getMonsterInDirection(this.currentDir);

        while (this.hero.getTotalHp() > 0 && monster.getHp() > 0) {
            fightRound(monster);
        }

        // no win
        if (this.hero.getTotalHp() <= 0) {
            return new FightResult(false, false, null);
        }

        // yes win
        final var levelUp = hero.gainXp(monster.getXpReward());
        final var drop = dropArtifact(monster);

        map.removeMonster(currentDir);
        map.moveHero(currentDir);
        currentDir = null;

        return new FightResult(true, levelUp, drop);
    }

    private void fightRound(Monster monster) {
        // hero attacks monster
        monster.takeDamage(this.hero.getTotalAttack());
        if (monster.isDead()) {
            return;
        }

        // monster 50% chance attacks hero
        if (random.nextBoolean()) {
            System.out.println("monster misses"); //#todo remove (debugging)
            return;
        }
        hero.takeDamage(monster.getAttack());
    }

    // misschien vanuit monster ook? -> gaat om de interne toestand van het monster
    // monster zou je een soort 'get artifact value' aan vragen oid.
    // berekening is monster. drop kans is game engine.
    // je kunt ook zeggen dat monster altijd een artifact heeft en je die soms krijgt
    // dan kun je al zien wat het monster heeft ook.
    private Artifact dropArtifact(Monster monster) {
        final var value = monster.getArtifactValue();
        final var drop = random.nextInt(5);
        return switch (drop) {
            case 0 ->
                Artifact.weapon(value);
            case 1 ->
                Artifact.armour(value);
            case 2 ->
                Artifact.helmet(value);
            default ->
                null;
        };
    }
}
