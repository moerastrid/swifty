package mazie.game;

import java.util.Random;
import mazie.model.Direction;
import mazie.model.GameMap;
import mazie.model.Hero;
import mazie.model.monster.Monster;

public class GameEngine {

    private final Hero hero;
    private final Random random;
    private final GameMap map;

    public GameEngine(Hero hero) {
        this(hero, new Random());
    }

    public GameEngine(Hero hero, Random random) {
        this.hero = hero;
        this.random = random;
        this.map = new GameMap(hero.getLevel());
    }

    public boolean gameOver() {
        return (heroWins() || heroDies());
    }

    public boolean heroWins() {
        return map.isHeroOnEdge();
    }

    public boolean heroDies() {
        return hero.isDead();
    }

    public boolean takeStep(Direction dir) {
        final var monster = map.getMonsterInDirection(dir);
        if (monster == null) {
            map.moveHero(dir);
            return true;
        }
        if (monster.isDead()) {
            map.removeMonster(dir);
            map.moveHero(dir);
            return true;
        }
        return false;
    }

    public Monster getMonster(Direction dir) {
        return map.getMonsterInDirection(dir);
    }

    public boolean escape() {
        return random.nextBoolean();
    }

    public int fight(Monster monster) {

        var damageToHero = 0;
        while (!hero.isDead() && !monster.isDead()) {
            damageToHero += fightRound(monster);
        }
        return damageToHero;
    }

    public boolean gainXp(int xp) {
        return hero.gainXp(xp);
    }

    private int fightRound(Monster monster) {
        monster.takeDamage(hero.getTotalAttack());
        return hero.takeDamage(monster.counterAttack());
    }
}
