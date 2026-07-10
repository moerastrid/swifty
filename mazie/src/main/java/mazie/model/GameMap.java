package mazie.model;

import java.util.Random;

import mazie.model.monster.Monster;
import mazie.model.monster.MonsterFactory;

public class GameMap {

    private final MonsterFactory factory;
    private final int size;
    private final Monster[][] monsters;
    private final Random random;
    private int heroX = 0;
    private int heroY = 0;

    public GameMap(int heroLevel) {
        this.factory = MonsterFactory.getInstance();
        this.random = new Random();
        this.size = ((heroLevel - 1) * 5 + 10 - (heroLevel % 2) + 2);
        this.heroX = size / 2;
        this.heroY = size / 2;
        this.monsters = new Monster[size][size];
        this.generateMonsters(random, heroLevel);
    }

    public boolean isHeroOnEdge() {
        return isEdge(this.heroX, this.heroY);
    }

    public Monster advance(Direction dir) {
        final var monster = this.getMonsterInDirection(dir);
        if (monster == null) {
            this.moveHero(dir);
        } else if (monster.isDead()) {
            this.removeMonster(dir);
            return advance(dir);
        }
        return monster;
    }

    private Monster getMonsterInDirection(Direction dir) {
        final var x = this.heroX + dir.dx;
        final var y = this.heroY + dir.dy;

        return (monsters[x][y]);
    }

    private void moveHero(Direction dir) {
        this.heroX += dir.dx;
        this.heroY += dir.dy;
    }

    private void removeMonster(Direction dir) {
        final var x = this.heroX + dir.dx;
        final var y = this.heroY + dir.dy;

        monsters[x][y] = null;
    }

    // public boolean isHeroNextToMonster(Direction dir) {
    //     return isMonster(this.heroX + dir.dx, this.heroY + dir.dy);
    // }
    private void generateMonsters(Random random, int heroLevel) {
        final int total = size * size;

        final int hardCount = total / 27;
        final int mediumCount = total / 12;
        final int easyCount = total / 9;

        for (int i = 0; i < easyCount; i++) {
            this.generateMonster(random, factory.newEasyMonster(heroLevel));
        }
        for (int i = 0; i < mediumCount; i++) {
            this.generateMonster(random, factory.newMediumMonster(heroLevel));
        }
        for (int i = 0; i < hardCount; i++) {
            this.generateMonster(random, factory.newHardMonster(heroLevel));
        }
    }

    private void generateMonster(Random random, Monster monster) {
        int x = random.nextInt(1, size - 2);
        int y = random.nextInt(1, size - 2);

        while (!isAvailable(x, y)) {
            x = random.nextInt(1, size - 2);
            y = random.nextInt(1, size - 2);
        }
        monsters[x][y] = monster;
    }

    private boolean isAvailable(int x, int y) {
        if (isHeroPos(x, y)) {
            return false;
        } else if (isEdge(x, y)) {
            return false;
        } else if (isMonster(x, y)) {
            return false;
        }
        return true;
    }

    private boolean isHeroPos(int x, int y) {
        return (this.heroX == x && this.heroY == y);
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
