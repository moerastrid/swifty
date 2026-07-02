package mazie.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import mazie.exception.FatalException;
import mazie.model.Hero;

public class SQLiteHeroRepository implements HeroRepository {

    private static final String DB_URL = "jdbc:sqlite:data/swingy.db";
    private final Connection connection;

    public SQLiteHeroRepository() {
        this.connection = setupConnection();
        this.createTables();
    }

    private Connection setupConnection() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new FatalException("Database connection fail", e);
        }
    }

    private void createTables() {
        String enableForeignKeys = "PRAGMA foreign_keys = ON;";

        String createHeroTable = """
            CREATE TABLE IF NOT EXISTS hero(
                id      INTEGER PRIMARY KEY AUTOINCREMENT,
                name    TEXT NOT NULL UNIQUE,
                type    TEXT NOT NULL CHECK(type IN ('FROG', 'HARE', 'BEAR')),
                level   INTEGER,
                xp      INTEGER,
                attack  INTEGER,
                defence INTEGER,
                hp      INTEGER
            );
        """;

        String createArtifactTable = """
            CREATE TABLE IF NOT EXISTS artifact(
                id      INTEGER PRIMARY KEY AUTOINCREMENT,
                name    TEXT,
                type    TEXT CHECK(type IN ('WEAPON', 'HELMET', 'ARMOUR')),
                value   INTEGER,
                hero_id INTEGER NOT NULL,
                FOREIGN KEY(hero_id) REFERENCES hero(id)
            );
        """;

        try (final var statement = connection.createStatement()) {
            statement.execute(enableForeignKeys);
            statement.execute(createHeroTable);
            statement.execute(createArtifactTable);
        } catch (SQLException e) {
            throw new FatalException("Error creating database tables", e);
        }
    }

    /*
    artifact query samenvoegen:

    SELECT h.*, a.slot, a.name, a.value
    FROM heroes h
    LEFT JOIN artifacts a ON a.hero_id = h.id
    */

    @Override
    public List<Hero> loadAllHeroes() {
        // #todo implement
        return null;
    }

    @Override
    public void save(Hero hero) {
        // #todo implement
    }

    @Override
    public void update(Hero hero) {
        // #todo implement
    }

    @Override
    public void delete(Hero hero) {
        // #todo implement
    }
}
