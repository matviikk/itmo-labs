package utility;

import java.sql.*;
public class DatabaseConnectionManager {
//    public static final String DB_URL = "jdbc:postgresql://pg:5432/";
//    public static final String DB_NAME = "studs";
//    private static String USER = "s342951";
//    private static String PASSWORD = "jRBZRXczxMxm6C7b";
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/";
    public static final String DB_NAME = "lab";
    private static String USER = "postgres";
    private static String PASSWORD = "admin";

    /**
     * Retrieves a database connection.
     *
     * @return A database connection.
     */
    public static Connection getConnection() {
        try {
            //return DriverManager.getConnection(DB_URL + DB_NAME);
            return DriverManager.getConnection(DB_URL + DB_NAME, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Closes a database connection.
     *
     * @param connection The database connection to close.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static Statement createStatement(Connection connection) {
        if (connection == null) {
            return null;
        }
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executes an SQL update statement using a given statement.
     *
     * @param statement The statement to execute.
     * @param sql       The SQL statement to execute.
     */
    public static void executeUpdate(Statement statement, String sql) {
        if (statement == null) {
            return;
        }
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes an SQL update statement using a given connection.
     *
     * @param connection The database connection.
     * @param sql        The SQL statement to execute.
     */
    public static void executeUpdate(Connection connection, String sql) {
        Statement statement = createStatement(connection);
        executeUpdate(statement, sql);
    }

    /**
     * Executes a prepared SQL update statement.
     *
     * @param statement The prepared statement to execute.
     * @return The result of executing the statement.
     */
    public static int executePrepareUpdate(PreparedStatement statement) {
        if (statement == null) {
            return -1;
        } else {
            try {
                return statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }
}
