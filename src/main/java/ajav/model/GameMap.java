package ajav.model;

import ajav.model.hero.Hero;
import ajav.model.hero.HeroType;

import java.util.Random;

public class GameMap {
    private final int size;
    private final CellType[][] cells;
    private final Random random;
    private final HeroType heroType;

    public GameMap(Hero hero) {
        int level = hero.getLevel();
        this.size = (level - 1) * 5 + 10 - (level % 2);
        this.cells = new CellType[size][size];
        this.random = new Random();
        this.heroType = hero.getType();
        setCells();
    }

    private void setCells() {

        this.cells[size / 2][size / 2] = CellType.HERO;

        int amountEasy = (size - 3) * 4 - 9;
        int amountMedium = (size - 2) * 3 - 12;
        int amountHard = (size - 2) * 2 - 35;
        if (amountHard < 0) {
            amountHard = 3;
        }
        int amountArtifacts = (size - 2) * 2 - 9;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == 0 || j == 0 || i == size - 1 || j == size - 1) {
                    cells[i][j] = CellType.WALL;
                } else if (cells[i][j] == null) {
                    cells[i][j] = CellType.EMPTY;
                }
            }
        }

        for (int i = 0; i < amountEasy; i++) {
            setRandomCell(CellType.MONSTER_EASY);
        }
        for (int i = 0; i < amountMedium; i++) {
            setRandomCell(CellType.MONSTER_MEDIUM);
        }
        for (int i = 0; i < amountHard; i++) {
            setRandomCell(CellType.MONSTER_HARD);
        }
        for (int i = 0; i < amountArtifacts; i++) {
            setRandomCell(CellType.ARTIFACT);
        }
    }

    private boolean isCellEmpty(int x, int y) {
        return cells[x][y] == CellType.EMPTY;
    }

    private void setRandomCell(CellType type) {
        int x = random.nextInt(size);
        int y = random.nextInt(size);

        while (!isCellEmpty(x, y)) {
            x = random.nextInt(size);
            y = random.nextInt(size);
        }
        cells[x][y] = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] == CellType.HERO)
                    sb.append(this.heroType.toString());
                else
                    sb.append(cells[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
