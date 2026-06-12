package utility;

import model.LabWork;

import java.sql.*;

import static utility.DatabaseConnectionManager.DB_URL;

public class DatabaseManager {
    private static final LabWorkDAO labWorkDAO = new LabWorkDAO();
    private static final UserDAO userDAO = new UserDAO();

    /**
     * Creates the database if it does not already exist and initializes tables.
     */
    public static void createDatabaseIfNotExists() {
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            if (connection != null) {
                boolean databaseExists = checkDatabaseExists(connection);
                if (!databaseExists) {
                    DatabaseConnectionManager.executeUpdate(connection, "CREATE DATABASE " + DatabaseConnectionManager.DB_NAME);
                    System.out.println("Create DB");
                } else {
                    System.out.println("Database exists");
                }
                createTablesIfNotExist(connection);
            } else {
                System.out.println("connection not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the database already exists.
     *
     * @param connection The database connection.
     * @return True if the database exists, false otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    private static boolean checkDatabaseExists(Connection connection) throws SQLException {
        return connection.getMetaData().getCatalogs()
                .next(); // Check if the database exists by attempting to move to the first entry
    }

    /**
     * Creates necessary tables if they do not already exist.
     *
     * @param connection The database connection.
     */
    public static void createTablesIfNotExist(Connection connection) {
        if (connection != null) {
            userDAO.createTableIfNotExist();
            labWorkDAO.createTableIfNotExist();
        } else {
            System.out.println("connection not found");
        }
    }
}
