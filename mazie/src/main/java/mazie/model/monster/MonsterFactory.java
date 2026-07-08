package mazie.model.monster;

import java.util.Random;

public class MonsterFactory {

    private static final MonsterFactory instance = new MonsterFactory();
    private static final Random RANDOM = new Random();

    private MonsterFactory() {
    }

    public static MonsterFactory getInstance() {
        return instance;
    }

    public Monster newEasyMonster(int heroLevel) {
        return switch (RANDOM.nextInt(3)) {
            case 0 ->
                new Library(heroLevel);
            case 1 ->
                new IKEA(heroLevel);
            default ->
                new Park(heroLevel);
        };
    }

    public Monster newMediumMonster(int heroLevel) {
        return switch (RANDOM.nextInt(5)) {
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

    public Monster newHardMonster(int heroLevel) {
        final var bound = (heroLevel > 5) ? 4 : 3;
        return switch (RANDOM.nextInt(bound)) {
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
