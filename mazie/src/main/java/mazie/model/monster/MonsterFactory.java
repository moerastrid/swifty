package mazie.model.monster;

import java.util.Random;

public class MonsterFactory {

    private static final MonsterFactory instance = new MonsterFactory();
    private final Random random = new Random();

    private static enum Difficulty {
        EASY,
        MEDIUM,
        HARD;
    }

    private MonsterFactory() {
    }

    public static MonsterFactory getInstance() {
        return instance;
    }

    public Monster generateMonster(int heroLevel) {

        return switch(this.pickDifficulty()) {
            case EASY ->
                newEasyMonster(heroLevel);
            case MEDIUM ->
                newMediumMonster(heroLevel);
            case HARD ->
                newHardMonster(heroLevel);
        };
    }

    private Difficulty pickDifficulty() {
        final var easy = 12;
        final var medium = 9;
        final var hard = 4;
        final var roll = random.nextInt(easy + medium + hard);

        if (roll < easy) {
            return Difficulty.EASY;
        }
        if (roll < (easy + medium)) {
            return Difficulty.MEDIUM;
        }
        return Difficulty.HARD;
    }

    private Monster newEasyMonster(int heroLevel) {
        return switch (random.nextInt(3)) {
            case 0 ->
                new Library(heroLevel);
            case 1 ->
                new IKEA(heroLevel);
            default ->
                new Park(heroLevel);
        };
    }

    private Monster newMediumMonster(int heroLevel) {
        return switch (random.nextInt(5)) {
            case 0 ->
                new Hairsalon(heroLevel);
            case 1 ->
                new Kanteen(heroLevel);
            case 2 ->
                new Office(heroLevel);
            case 3 ->
                new Teams(heroLevel);
            default ->
                new Bus(heroLevel);
        };
    }

    private Monster newHardMonster(int heroLevel) {
        final var bound = (heroLevel >= 5) ? 4 : 3;
        return switch (random.nextInt(bound)) {
            case 0 ->
                new Swimmingpool(heroLevel);
            case 1 ->
                new TeamBuilding(heroLevel);
            case 2 ->
                new Supermarket(heroLevel);
            default ->
                new Vim(heroLevel);
        };
    }
}
