package mazie.game;

import mazie.model.Artifact;
import mazie.model.GameMap;
import mazie.model.Hero;
import mazie.model.monster.Monster;

import java.util.Random;
import mazie.view.GameView;

public class GameEngine {

    private final GameView view;
    private final Hero hero;
    private final GameMap map;
    private final Random random;

    private boolean playing;

    public GameEngine(GameView view, Hero hero) {
        this.view = view;
        this.hero = hero;
        this.map = new GameMap(hero.getLevel());
        this.random = new Random();
        this.playing = true;
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

    public Hero getHero() {
        return hero;
    }

    public void playTurn() {
        System.out.println("current map:\n" + map.toString()); //#todo remove

        final var dir = view.askDirection(hero);
        final var monster = map.getMonsterInDirection(dir);
        if (monster == null) {
            map.moveHero(dir);
            view.showEmptyStep();
            return;
        }

        final boolean feelingAggressive = view.askFightMonster(hero, monster);
        if (!feelingAggressive) {
            final var escaped = random.nextBoolean();
            view.showRunSuccess(monster, escaped);
            if (escaped) {
                return;
            }
        }

        final var damageToHero = fight(monster);

        if (hero.isDead()) {
            playing = false;
            return;
        }

        map.removeMonster(dir);
        map.moveHero(dir);
        view.showFightSummary(damageToHero, hero, monster);

        final var lvlUp = hero.gainXp(monster.getXpReward());

        if (lvlUp) {
            view.showLevelUp(hero);
        }

        handleArtifact(monster);
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
    }
}





//    public String getMapString() {
//        return this.map.toString();
//    }
//
//    public Monster move(Direction dir) {
//        final var monster = this.map.getMonsterInDirection(dir);
//        if (monster == null) {
//            this.map.moveHero(dir);
//            return null;
//        }
//        this.pendingDir = dir;
//        return monster;
//    }
//
//    public boolean runAway() {
//        final var escaped = this.random.nextBoolean();
//        if (escaped) {
//            pendingDir = null;
//        }
//        return escaped;
//    }
//
//    public boolean win() {
//        return this.map.isHeroOnEdge();
//    }
//
//    public FightResult fight() {
//        final var monster = this.map.getMonsterInDirection(this.pendingDir);
//
//        var totalDamageToHero = 0;
//        while (!this.hero.isDead() && !monster.isDead()) {
//            totalDamageToHero += fightRound(monster);
//        }
//
//        if (this.hero.isDead()) {
//            return new FightResult(false, false, null, totalDamageToHero);
//        }
//
//        final var levelUp = hero.gainXp(monster.getXpReward());
//        final var drop = dropArtifact(monster);
//
//        this.map.removeMonster(pendingDir);
//        this.map.moveHero(pendingDir);
//
//        pendingDir = null;
//
//        return new FightResult(true, levelUp, drop, totalDamageToHero);
//    }
//
//    private int fightRound(Monster monster) {
//        // hero attacks monster
//        monster.takeDamage(this.hero.getTotalAttack());
//        if (monster.isDead()) {
//            return 0;
//        }
//
//        // monster 50% chance attacks hero
//        if (random.nextBoolean()) {
//            return 0;
//        }
//        return hero.takeDamage(monster.getAttack());
//    }
//
//    private Artifact dropArtifact(Monster monster) {
//        final var value = monster.getArtifactValue();
//        return Artifact.createArtifact(value);
//    }
//}
