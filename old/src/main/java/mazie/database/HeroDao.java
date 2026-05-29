package mazie.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import mazie.model.hero.Hero;
import mazie.model.hero.HeroType;

public class HeroDao {
	private static final String DB_URL = "jdbc:sqlite:data/swingy.db";
	private Connection connection;

	public HeroDao() {
		try {
			File dataDir = new File("data");
			if (!dataDir.exists())
				dataDir.mkdir();

			Class.forName("org.sqlite.JDBC");
			
			connection = DriverManager.getConnection(DB_URL);

			this.createTables();
		} catch (ClassNotFoundException e) {
			System.err.println("SQLite JDBC driver not found");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("Database connection fail");
			e.printStackTrace();
		}
	}


	private void createTables() throws SQLException {
		String createHeroTable = """
				CREATE TABLE IF NOT EXISTS heroes (
				id		INTEGER PRIMARY KEY AUTOINCREMENT,
				name	TEXT NOT NULL UNIQUE,
				type	TEXT NOT NULL,
				level	INTEGER NOT NULL,
				xp		INTEGER NOT NULL,
				attack	INTEGER NOT NULL,
				defense	INTEGER NOT NULL,
				hp		INTEGER NOT NULL
				)
		""";
		
		try (Statement statement = connection.createStatement()) {
			statement.execute(createHeroTable);
		}
	}

	public void save(Hero hero) throws SQLException {
		String sql = """
				INSERT INTO heroes
				(name, type, level, xp, attack, defense, hp)
				VALUES (?, ?, ?, ?, ?, ?, ?)
		""";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, hero.getName());
			preparedStatement.setString(2, hero.getType().toString());
			preparedStatement.setInt(3, hero.getLevel());
			preparedStatement.setInt(4, hero.getXp());
			preparedStatement.setInt(5, hero.getAttack());
			preparedStatement.setInt(6, hero.getDefense());
			preparedStatement.setInt(7, hero.getHp());
			preparedStatement.executeUpdate();
		}
	}

	public Hero findByName(String name) throws SQLException {
		String sql = "SELECT * FROM heroes WHERE name = ?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return this.buildHeroFromResultSet(resultSet);
				}
			}
		}
		return null;
	}

	public Hero findById(int id) throws SQLException {
		String sql = "SELECT * FROM heroes WHERE id = ?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, id);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return this.buildHeroFromResultSet(resultSet);
				}
			}
		}
		return null;
	}

	// public List<Hero> findAll() throws SQLException {
		
	// }

	private Hero buildHeroFromResultSet(ResultSet resultSet) throws SQLException {
		resultSet.getString("name");

		return new Hero(
			resultSet.getInt("id"),
			resultSet.getString("name"),
			defineHeroTypeFromEmoji(resultSet.getString("type")),
			resultSet.getInt("level"),
			resultSet.getInt("xp"),
			resultSet.getInt("attack"),
			resultSet.getInt("defense"),
			resultSet.getInt("hp"));
	}

	private HeroType defineHeroTypeFromEmoji(String emoji) {
		return switch(emoji) {
			case "🐧" -> HeroType.PENGUIN;
			case "🐸" -> HeroType.FROG;
			case "🐻" -> HeroType.BEAR;
			case "🐰" -> HeroType.HARE;
			case "🐢" -> HeroType.TURTLE;
			default -> throw new IllegalArgumentException("Invalid hero type emoji");
		};
	}
}