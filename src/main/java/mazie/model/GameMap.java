package mazie.model;

import mazie.model.monster.Monster;
import mazie.model.monster.MonsterFactory;

import java.util.Random;

public class GameMap {

    private final MonsterFactory factory;
    private final int size;
    private final Monster[][] monsters;
    private final Random random;
    private int heroX;
    private int heroY;

    public GameMap(int heroLevel) {
        this.factory = MonsterFactory.getInstance();
        this.random = new Random();
        this.size = ((heroLevel - 1) * 5 + 10 - (heroLevel % 2) + 2);
        this.heroX = size / 2;
        this.heroY = size / 2;
        this.monsters = new Monster[size][size];
        this.generateMonsters(heroLevel);
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

    private void generateMonsters(int heroLevel) {
        final int total = (int) (0.23 * (size - 2) * (size - 2));

        for (int i = 0; i < total; i++) {
            final var monster = factory.generateMonster(heroLevel);
            this.placeOnMap(monster);
        }
    }

    private void placeOnMap(Monster monster) {
        int x = random.nextInt(1, size - 2);
        int y = random.nextInt(1, size - 2);

        while (!isAvailable(x, y)) {
            x = random.nextInt(1, size - 2);
            y = random.nextInt(1, size - 2);
        }
        this.monsters[x][y] = monster;
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
