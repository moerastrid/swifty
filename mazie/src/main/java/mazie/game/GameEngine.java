package mazie.game;

import java.util.Random;

import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.GameMap;
import mazie.model.Hero;
import mazie.model.Monster;

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

        if (monster == null) {
            map.moveHero(dir);
            this.currentDir = null;
            return null;
        } else {
            return monster;
        }
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
        int damageToMonster = this.hero.getTotalAttack() - monster.getDefence();
        System.out.println("damage to monster: " + damageToMonster); //#todo remove (debugging)

        if (damageToMonster <= 0) {
            damageToMonster = 1;
        }

        if (damageToMonster > 0) {
            final var newMonsterHp = monster.getHp() - damageToMonster;
            monster.setHp(newMonsterHp);

            if (monster.getHp() <= 0) {
                return;
            }
        }

        // monster attacks hero
        int damageToHero = monster.getAttack() - this.hero.getTotalDefence();

        if (damageToHero <= 0) {
            damageToHero = 1;
        }

        // monster 50% chance attacks hero
        if (random.nextBoolean()) {
            System.out.println("monster misses"); //#todo remove (debugging)
            return;
        }
        System.out.println("damage to hero: " + damageToHero); //#todo remove (debugging)

        final var newHp = this.hero.getHp() - damageToHero;
        this.hero.setHp(newHp);

    }

    private Artifact dropArtifact(Monster monster) {
        int value = (monster.getAttack() + monster.getDefence()) / 4;
        if (value < 1) {
            value = 1;
        }
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
