package ajav.model;

import ajav.model.hero.Hero;
import ajav.model.hero.HeroType;

import java.util.Random;

public class GameMap {
    private final int size;
    private final CellType[][] cells;
    private final Random random;
    private final HeroType heroType;
    private int pos_x;
    private int pos_y;

    public GameMap(Hero hero) {
        final int level = hero.getLevel();
        this.size = (level - 1) * 5 + 10 - (level % 2);
        this.cells = new CellType[size][size];
        this.random = new Random();
        this.heroType = hero.getType();
        this.pos_x = size / 2;
        this.pos_y = size / 2;
        setCells();
    }

    public CellType getUp() {
        return cells[pos_x][pos_y - 1];
    }

    public CellType getDown() {
        return cells[pos_x][pos_y + 1];
    }

    public CellType getLeft() {
        return cells[pos_x - 1][pos_y];
    }

    public CellType getRight() {
        return cells[pos_x + 1][pos_y];
    }

    public CellType moveUp() {
        if (pos_y > 0) {
            cells[pos_x][pos_y] = CellType.EMPTY;
            final var cellType = getUp();
            pos_y -= 1;
            cells[pos_x][pos_y] = CellType.HERO;
            return cellType;
        }
        return null;
        //else
        //    throw new RuntimeException("Cannot move up, out of bounds");
    }

    public CellType moveDown() {
        if (pos_y < size - 1) {
            cells[pos_x][pos_y] = CellType.EMPTY;
            final var cellType = getDown();
            pos_y += 1;
            cells[pos_x][pos_y] = CellType.HERO;
            return cellType;
        }
        return null;
        //else
        //    throw new RuntimeException("Cannot move down, out of bounds");
    }

    public CellType moveLeft() {
        if (pos_x > 0) {
            cells[pos_x][pos_y] = CellType.EMPTY;
            final var cellType = getLeft();
            pos_x -= 1;
            cells[pos_x][pos_y] = CellType.HERO;
            return cellType;
        }
        return null;
        //else
        //    throw new RuntimeException("Cannot move left, out of bounds");
    }

    public CellType moveRight() {
        if (pos_x < size - 1) {
            cells[pos_x][pos_y] = CellType.EMPTY;
            final var cellType = getRight();
            pos_x += 1;
            cells[pos_x][pos_y] = CellType.HERO;
            return cellType;
        }
        return null;
        //else
        //    throw new RuntimeException("Cannot move right, out of bounds");
    }

    private void setCells() {

        int amountEasy = (size - 3) * 5 - 16;
        int amountMedium = (size - 2) * 3 - 12;
        int amountHard = (size - 2) * 2 - 35;
        if (amountHard < 0) {
            amountHard = 3;
        }
        int amountArtifacts = (size - 2) * 2 - 9;

        setDefinedCells();
        setRandomCells(CellType.MONSTER_EASY, amountEasy);
        setRandomCells(CellType.MONSTER_MEDIUM, amountMedium);
        setRandomCells(CellType.MONSTER_HARD, amountHard);
        setRandomCells(CellType.ARTIFACT, amountArtifacts);
    }

    private void setDefinedCells() {
        this.cells[pos_y][pos_x] = CellType.HERO;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == 0 || j == 0 || i == size - 1 || j == size - 1) {
                    cells[j][i] = CellType.WALL;
                } else if (cells[j][i] == null) {
                    cells[j][i] = CellType.EMPTY;
                }
            }
        }
    }

    private boolean isCellEmpty(int x, int y) {
        return cells[x][y] == CellType.EMPTY;
    }

    private void setRandomCells(CellType type, int amount) {
        for (int i = 0; i < amount; i++) {
            setRandomCell(type);
        }
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
