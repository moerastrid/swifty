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
    private final Random random = new Random(System.currentTimeMillis());
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
        return this.random.nextBoolean();
    }

    public boolean win() {
        return this.map.isHeroOnEdge();
    }

    public FightResult fight() {
        final var monster = this.map.getMonsterInDirection(this.currentDir);

        while(this.hero.getTotalHp() > 0 && monster.getHp() > 0) {
            fightRound(monster);
        }

        // no win
        if (this.hero.getTotalHp() <= 0) {
            return new FightResult(false, false, null);
        }

        // yes win
        final var levelUp = hero.gainXp(monster.getXpReward());
        final var drop = dropArtifact(monster.getXpReward());

        map.removeMonster(currentDir);
        map.moveHero(currentDir);
        currentDir = null;

        return new FightResult(true, levelUp, drop);
    }

    private void fightRound(Monster monster) {

        // hero attacks monster
        final int damageToMonster = this.hero.getTotalAttack() - monster.getDefence();
        System.out.println("damage to monster: " + damageToMonster);

        if (damageToMonster > 0) {
            final var newMonsterHp = monster.getHp() - damageToMonster;
            monster.setHp(newMonsterHp);

            if (monster.getHp() <= 0)
                return;
        }

        // monster attacks hero
        final int damageToHero = monster.getAttack() - this.hero.getTotalDefence();
        System.out.println("damage to hero: " + damageToHero);

        // monster 50% chance attacks hero
        if (random.nextBoolean() || damageToHero <= 0) {
            return;
        }

        final var newHp = this.hero.getHp() - damageToHero;
        this.hero.setHp(newHp);
        
    }

    private Artifact dropArtifact(int xpReward) {
        final int value = xpReward / 10 - 2;
        final var drop = random.nextInt(4);
        return switch (drop) {
            case 0 -> Artifact.weapon(value);
            case 1 -> Artifact.armour(value);
            case 2 -> Artifact.helmet(value);
            default -> null;
        };
    }

    // public boolean fought() {
    //     final var monster = this.map.getMonsterInDirection(this.currentDir);
    //     var won = false;

    //     while(this.hero.getHp() > 0 && monster.getHp() > 0) {

    //         simulateFightRound(monster);

    //         System.out.println("--- STATS ---");
    //         System.out.println(hero.toString());
    //         System.out.println(monster.toString());
    //         System.out.println("-------------");

    //     }

    //     if (monster.getHp() <= 0 && this.hero.getTotalHp() != 0) {
    //         won = true;
    //     }

    //     if (won == true) {
    //         this.map.removeMonster(this.currentDir);
    //         this.move(this.currentDir);
    //     }

    //     return won;
    // }

    // private void simulateFightRound(Monster monster) {
        
    //     // hero attacks monster
    //     System.out.println(hero.getName() + " attacks");
    //     final int damageToMonster = this.hero.getTotalAttack() - monster.getDefence();

    //     if (damageToMonster <= 0) {
    //         System.out.println("... but nothing happens");
    //     } else {
    //         System.out.println("hero does damage to monster: " + damageToMonster);
    //         final var newHp = monster.getHp() - damageToMonster;
    //         monster.setHp(newHp);

    //         if (monster.getHp() < 0) {
    //             System.out.println("monster died");
    //             this.hero.gainXp(monster.getXpReward());
    //             return;
    //         }
    //     }

    //     // monster 50% chance attacks hero
    //     System.out.println(monster.getName() + " attacks");
    //     final int damageToHero = monster.getAttack() - this.hero.getTotalDefence();

    //     if (random.nextBoolean() || damageToHero <= 0) {
    //         System.out.println("... but nothing happens");
    //         return;
    //     }

    //     System.out.println("monster does damage to hero: " + damageToHero);
    //     final var newHp = this.hero.getHp() - damageToHero;
    //     this.hero.setHp(newHp);


    // }

}
