package mazie.model;

import mazie.model.monster.Monster;
import mazie.model.monster.MonsterFactory;

import java.util.Random;

public class GameMap {

    private final int internalSize;
    private final int size;
    private final Monster[][] monsters;
    private int heroX;
    private int heroY;

    public GameMap(int heroLevel) {
        internalSize = (heroLevel - 1) * 5 + 10 - (heroLevel % 2);
        size = internalSize + 2;
        heroX = size / 2;
        heroY = size / 2;
        monsters = new Monster[size][size];
        generateMonsters(heroLevel);
    }

    public boolean isHeroOnEdge() {
        return isEdge(heroX, heroY);
    }

    public Monster getMonsterInDirection(Direction dir) {
        final var x = heroX + dir.dx;
        final var y = heroY + dir.dy;

        return (monsters[x][y]);
    }

    public void moveHero(Direction dir) {
        final var x = heroX + dir.dx;
        final var y = heroY + dir.dy;

        if (isHeroOnEdge() || isMonster(x, y)) {
            throw new IllegalStateException("GameMap: invalid hero move");
        }

        heroX = x;
        heroY = y;
    }

    public void removeMonster(Direction dir) {
        final var x = heroX + dir.dx;
        final var y = heroY + dir.dy;

        if (!isMonster(x, y)) {
            throw new IllegalStateException("Can't remove a monster that doesn't exist");
        }
        monsters[x][y] = null;
    }

    private void generateMonsters(int heroLevel) {
        final int total = (int) (0.23 * internalSize * internalSize);
        final var factory = MonsterFactory.getInstance();
        final var random = new Random();

        for (int i = 0; i < total; i++) {
            final var monster = factory.generateMonster(heroLevel);
            placeOnMap(monster, random);
        }
    }

    private void placeOnMap(Monster monster, Random random) {
        int x = random.nextInt(1, size - 1);
        int y = random.nextInt(1, size - 1);

        while (!isAvailable(x, y)) {
            x = random.nextInt(1, size - 1);
            y = random.nextInt(1, size - 1);
        }
        monsters[x][y] = monster;
    }

    private boolean isAvailable(int x, int y) {
        if (isHeroPos(x, y)) {
            return false;
        } else if (isEdge(x, y)) {
            return false;
        } else {
            return !isMonster(x, y);
        }
    }

    private boolean isHeroPos(int x, int y) {
        return (heroX == x && heroY == y);
    }

    private boolean isEdge(int x, int y) {
        return (x == 0 || y == 0 || x == size - 1 || y == size - 1);
    }

    private boolean isMonster(int x, int y) {
        return (monsters[x][y] != null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (isHeroPos(j, i)) {
                    sb.append("H ");
                } else if (isMonster(j, i)) {
                    sb.append("M ");
                } else if (isEdge(j, i)) {
                    sb.append("~ ");
                } else {
                    sb.append(". ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
