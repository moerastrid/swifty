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

    private boolean playing = true;

    public GameEngine(Hero hero) {
        this(hero, new Random());
    }

    public GameEngine(Hero hero, Random random) {
        this.hero = hero;
        this.random = random;
        this.map = new GameMap(hero.getLevel());
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean heroWins() {
        return map.isHeroOnEdge();
    }

    public boolean heroDies() {
        return hero.isDead();
    }

    public boolean takeStep(Direction dir) {
        final var monster = map.getMonsterInDirection(dir);
        if (monster != null) {
            return false;
        }
        map.moveHero(dir);
        if (heroWins()) {
            playing = false;
        }
        return true;
    }

    public Monster getMonster(Direction dir) {
        return map.getMonsterInDirection(dir);
    }

    public boolean escape() {
        return random.nextBoolean();
    }

    public int fight(Direction dir) {
        final var monster = map.getMonsterInDirection(dir);

        var damageToHero = 0;
        while (!hero.isDead() && !monster.isDead()) {
            damageToHero += fightRound(monster);
        }

        if (hero.isDead()) {
            playing = false;
        }
        if (monster.isDead()) {
            map.removeMonster(dir);
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



