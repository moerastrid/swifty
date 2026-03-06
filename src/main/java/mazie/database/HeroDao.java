package mazie.database;

import java.beans.Statement;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HeroDao {
	private static final string DB_URL = "jdbc:sqlite:data/swingy.db";
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
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				name TEXT NOT NULL UNIQUE
				)
		""";
		
		try (Statement statement = connection.createStatement()) {
			statement.execute(createHeroTable);
		}
	}

	public void save(Hero hero) throws SQLException {
		String sql = """
				INSERT INTO heroes
				(name)
				VALUES (?)
		""";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, hero.getName());

			preparedStatement.executeUpdate();
		}
	}

	public Hero findByName(String name) {
		String sql = "SELECT * FROM heroes WHERE name = ?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return this.buildHeroFromResultSet(resultSet);
				}
			}
		}
	}

	public List<Hero> findAll() throws SQLException {
		
	}

	// private Hero buildHeroFromResultSet(ResultSet resultSet) {
	// 	String 
	// }
}