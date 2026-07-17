package mazie.game;

import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.GameMap;
import mazie.model.Hero;
import mazie.model.monster.Monster;

import java.util.Random;

public class GameEngine {

    private final Hero hero;
    private final GameMap map;
    private final Random random = new Random();
    private Direction pendingDir = null;

    public GameEngine(Hero hero) {
        this.hero = hero;
        this.map = new GameMap(hero.getLevel());
    }

    public String getMapString() {
        return this.map.toString();
    }

    public Monster move(Direction dir) {
        final var monster = this.map.getMonsterInDirection(this.pendingDir);
        if (monster == null) {
            this.map.moveHero(dir);
            return null;
        }
        this.pendingDir = dir;
        return monster;
    }

    public boolean runAway() {
        final var escaped = this.random.nextBoolean();
        if (escaped) {
            pendingDir = null;
        }
        return escaped;
    }

    public boolean win() {
        return this.map.isHeroOnEdge();
    }

    public FightResult fight() {
        final var monster = this.map.getMonsterInDirection(this.pendingDir);

        var totalDamageToHero = 0;
        while (!this.hero.isDead() && !monster.isDead()) {
            totalDamageToHero += fightRound(monster);
        }

        if (this.hero.isDead()) {
            return new FightResult(false, false, null, totalDamageToHero);
        }

        final var levelUp = hero.gainXp(monster.getXpReward());
        final var drop = dropArtifact(monster);

        this.map.removeMonster(pendingDir);
        this.map.moveHero(pendingDir);

        pendingDir = null;

        return new FightResult(true, levelUp, drop, totalDamageToHero);
    }

    private int fightRound(Monster monster) {
        // hero attacks monster
        monster.takeDamage(this.hero.getTotalAttack());
        if (monster.isDead()) {
            return 0;
        }

        // monster 50% chance attacks hero
        if (random.nextBoolean()) {
            return 0;
        }
        return hero.takeDamage(monster.getAttack());
    }

    private Artifact dropArtifact(Monster monster) {
        final var value = monster.getArtifactValue();
        return Artifact.createArtifact(value);
    }
}
