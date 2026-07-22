package mazie.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import mazie.exception.DuplicateNameException;
import mazie.exception.FatalException;
import mazie.exception.RepositoryException;
import mazie.model.ArtifactType;
import mazie.model.Hero;
import mazie.model.HeroType;

public class SQLiteHeroRepository implements HeroRepository {
    private static final String DB_URL = "jdbc:sqlite:data/swingy.db";
    private static final String DATA_DIR = "data";

    private static final String FATAL_DATABASE_CONNECTION = "Database connection fail";
    private static final String FATAL_DATABASE_ACCESS = "Database folder issue";
    private static final String FATAL_DATABASE_TABLES = "Database create tables";
    private static final String HERO_NO_ID = "hero with no ID";

    private static final String FOREIGN_KEYS_ON_SQL = "PRAGMA foreign_keys = ON;";

    private static final String HERO_TYPE = Arrays.stream(HeroType.values()).map(type -> "'%s'".formatted(type.name())).collect(Collectors.joining(", "));
    private static final String ARTIFACT_TYPE = Arrays.stream(ArtifactType.values()).map(type -> "'%s'".formatted(type.name())).collect(Collectors.joining(", "));

    private static final String CREATE_HERO_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS hero(
                    id      INTEGER PRIMARY KEY AUTOINCREMENT,
                    name    TEXT NOT NULL UNIQUE CHECK(length(name) BETWEEN 2 AND 30),
                    type    TEXT NOT NULL CHECK(type IN (%s)),
                    level   INTEGER NOT NULL CHECK(level >= 1),
                    xp      INTEGER NOT NULL CHECK(xp >= 0),
                    attack  INTEGER NOT NULL CHECK(attack >= 0),
                    defence INTEGER NOT NULL CHECK(defence >= 0),
                    hp      INTEGER NOT NULL
                );
            """.formatted(HERO_TYPE);
    private static final String CREATE_ARTIFACT_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS artifact(
                    id      INTEGER PRIMARY KEY AUTOINCREMENT,
                    name    TEXT NOT NULL,
                    type    TEXT NOT NULL CHECK(type IN (%s)),
                    value   INTEGER NOT NULL CHECK(value >= 0),
                    hero_id INTEGER NOT NULL,
                    FOREIGN KEY(hero_id) REFERENCES hero(id)
                );
            """.formatted(ARTIFACT_TYPE);
    private static final String LOAD_HEROES_SQL = """
            SELECT h.*, a.name AS a_name, a.type AS a_type, a.value AS a_value, a.hero_id AS a_hero_id
            FROM hero h
            LEFT JOIN artifact a ON a.hero_id = h.id;
            """;
    private static final String INSERT_HERO_SQL = """
            INSERT INTO hero(name, type, level, xp, attack, defence, hp)
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;
    private static final String INSERT_ARTIFACT_SQL = """
            INSERT INTO artifact(name, type, value, hero_id)
            VALUES (?, ?, ?, ?);
            """;
    private static final String UPDATE_HERO_SQL = """
            UPDATE hero
            SET name = ?, type = ?, level = ?, xp = ?, attack = ?, defence = ?, hp = ?
            WHERE id = ?;
            """;
    private static final String DELETE_HERO_SQL = """
            DELETE FROM hero
            WHERE id = ?;
            """;
    private static final String DELETE_ARTIFACTS_SQL = """
            DELETE FROM artifact
            WHERE hero_id = ?;
            """;

    private final Connection connection;

    public SQLiteHeroRepository() {
        this.connection = setupConnection();
        setAutoCommit();
        createTables();
    }

    private Connection setupConnection() {
        try {
            setupDataDir();
        } catch (SecurityException e) {
            throw new FatalException(FATAL_DATABASE_ACCESS);
        }
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new FatalException(FATAL_DATABASE_CONNECTION, e);
        }
    }

    private void setupDataDir() {
        final var dataDir = new File(DATA_DIR);
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            throw new FatalException(FATAL_DATABASE_ACCESS);
        }
        if (!dataDir.isDirectory() || !dataDir.canRead() || !dataDir.canWrite()) {
            throw new FatalException(FATAL_DATABASE_ACCESS);
        }
    }

    private void setAutoCommit() {
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new FatalException(FATAL_DATABASE_CONNECTION, e);
        }
    }

    private void createTables() {
        try (final var statement = connection.createStatement()) {
            statement.execute(FOREIGN_KEYS_ON_SQL);
            statement.execute(CREATE_HERO_TABLE_SQL);
            statement.execute(CREATE_ARTIFACT_TABLE_SQL);
            commitConnection();
        } catch (SQLException e) {
            throw new FatalException(FATAL_DATABASE_TABLES, e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new FatalException(FATAL_DATABASE_CONNECTION, e);
        }
    }

    @Override
    public Map<Integer, Hero> loadAllHeroes() {
        try (final var statement = connection.createStatement()) {
            final var resultSet = statement.executeQuery(LOAD_HEROES_SQL);
            commitConnection();
            return SQLiteHeroMapper.mapHeroes(resultSet);
        } catch (SQLException e) {
            rollbackConnection();
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(Hero hero) {
        try {
            insertHero(hero);
            insertArtifacts(hero);
            commitConnection();
        } catch (SQLiteException e) {
            rollbackConnection();
            if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
                throw new DuplicateNameException(hero.getName());
            }
            throw new RepositoryException(e.getMessage(), e);
        } catch (SQLException e) {
            rollbackConnection();
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    private void insertHero(Hero hero) throws SQLException {
        try (final var statement = connection.prepareStatement(INSERT_HERO_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, hero.getName());
            statement.setString(2, hero.getType().name());
            statement.setInt(3, hero.getLevel());
            statement.setInt(4, hero.getXp());
            statement.setInt(5, hero.getAttack());
            statement.setInt(6, hero.getDefence());
            statement.setInt(7, hero.getHp());
            statement.executeUpdate();

            final var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                hero.setId(keys.getInt(1));
            } else {
                throw new SQLException(HERO_NO_ID);
            }
        }
    }

    private void insertArtifacts(Hero hero) throws SQLException {
        if (hero.getArtifacts().isEmpty()) {
            return;
        }
        try (final var statement = connection.prepareStatement(INSERT_ARTIFACT_SQL)) {
            for (final var artifact : hero.getArtifacts()) {
                statement.setString(1, artifact.name());
                statement.setString(2, artifact.type().name());
                statement.setInt(3, artifact.value());
                statement.setInt(4, hero.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    @Override
    public void update(Hero hero) {
        if (hero.getId() == 0) {
            throw new RepositoryException(HERO_NO_ID);
        }

        try {
            deleteArtifacts(hero);
            updateHero(hero);
            insertArtifacts(hero);
            commitConnection();
        } catch (SQLException e) {
            rollbackConnection();
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    private void updateHero(Hero hero) throws SQLException {
        try (final var statement = connection.prepareStatement(UPDATE_HERO_SQL)) {
            statement.setString(1, hero.getName());
            statement.setString(2, hero.getType().name());
            statement.setInt(3, hero.getLevel());
            statement.setInt(4, hero.getXp());
            statement.setInt(5, hero.getAttack());
            statement.setInt(6, hero.getDefence());
            statement.setInt(7, hero.getHp());
            statement.setInt(8, hero.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Hero hero) {
        if (hero.getId() == 0) {
            throw new RepositoryException(HERO_NO_ID);
        }

        try {
            deleteArtifacts(hero);
            deleteHero(hero);
            commitConnection();
        } catch (SQLException e) {
            rollbackConnection();
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    private void deleteHero(Hero hero) throws SQLException {
        try (final var statement = connection.prepareStatement(DELETE_HERO_SQL)) {
            statement.setInt(1, hero.getId());
            statement.executeUpdate();
        }
    }

    private void deleteArtifacts(Hero hero) throws SQLException {
        try (final var statement = connection.prepareStatement(DELETE_ARTIFACTS_SQL)) {
            statement.setInt(1, hero.getId());
            statement.executeUpdate();
        }
    }

    private void rollbackConnection() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new FatalException(FATAL_DATABASE_CONNECTION, e);
        }
    }

    private void commitConnection() {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new FatalException(FATAL_DATABASE_CONNECTION, e);
        }
    }
}
