package utility;

import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static utility.DatabaseConnectionManager.*;

/**
 * The LabWorkDAO class provides methods for interacting with the labWorks table in the database.
 * It handles labWork creation, retrieval, updating, and removal.
 */
public class LabWorkDAO {
    private static final String SELECT_ALL_LABWORKS_SQL = "SELECT * FROM labworks";
    private static final String CREATE_LABWORKS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS labworks (" +
            "id SERIAL PRIMARY KEY," +
            "name VARCHAR NOT NULL," +
            "coordinates_x DOUBLE PRECISION NOT NULL," +
            "coordinates_y DOUBLE PRECISION NOT NULL," +
            "creation_date DATE NOT NULL," +
            "minimalPoint DOUBLE PRECISION NOT NULL," +
            "averagePoint INT," +
            "difficulty VARCHAR(20)," +
            "person_name VARCHAR," +
            "person_birthday TIMESTAMP," +
            "person_height INT," +
            "person_location_x DOUBLE PRECISION," +
            "person_location_y BIGINT," +
            "person_location_z DOUBLE PRECISION," +
            "person_location_name VARCHAR," +
            "user_id INT," +
            "FOREIGN KEY (user_id) REFERENCES users(id))";
    private static final String INSERT_LABWORK_SQL = "INSERT INTO labWorks (" +
            " name," +
            " coordinates_x," +
            " coordinates_y," +
            " creation_date," +
            " minimalPoint," +
            " averagePoint," +
            " difficulty," +
            " person_name," +
            " person_birthday," +
            " person_height," +
            " person_location_x," +
            " person_location_y," +
            " person_location_z," +
            " person_location_name," +
            " user_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String REMOVE_LABWORKS_SQL = "DELETE FROM labworks WHERE id = ?";
    private static final String REMOVE_LABWORKS_BY_USER_ID_SQL = "DELETE FROM labworks WHERE user_id = ?";

    private static final String REMOVE_LABWORKS_BY_DIFFICULTY_SQL = "DELETE FROM labworks WHERE difficulty = ?";
    private static final String CHECK_LABWORK_OWNERSHIP_SQL = "SELECT user_id FROM labworks WHERE id = ?";
    private static final String UPDATE_LABWORK_SQL = "UPDATE labworks SET " +
            "name = ?, " +
            "coordinates_x = ?, " +
            "coordinates_y = ?, " +
            "creation_date = ?, " +
            "minimalPoint = ?, " +
            "averagePoint = ?, " +
            "difficulty = ?, " +
            "person_name = ?, " +
            "person_birthday = ?, " +
            "person_height = ?, " +
            "person_location_x = ?, " +
            "person_location_y = ?, " +
            "person_location_z = ?, " +
            "person_location_name = ?, " +
            "user_id = ? " +
            "WHERE id = ?";
    private static final String REMOVE_GREATER_BY_AVERAGE_POINT_SQL = "DELETE FROM labworks WHERE averagePoint > ?";

    private static final String REMOVE_LOWER_BY_AVERAGE_POINT_SQL = "DELETE FROM labworks WHERE averagePoint < ?";

    /**
     * Adds a new labWork to the database.
     *
     * @param labWork The labWork to be added.
     * @param userId The ID of the user adding the labWork.
     * @return The ID of the newly added labWork if successful, otherwise -1.
     */
    public int addLabWork(LabWork labWork, int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_LABWORK_SQL, Statement.RETURN_GENERATED_KEYS)) {
            set(statement, labWork, userId);
            System.out.println(statement);
            int rowsAffected = executePrepareUpdate(statement);
            if (rowsAffected > 0) {
                // Get the generated keys (which include the ID of the newly added labWork)
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    // Return the ID of the newly added labWork
                    return generatedKeys.getInt(1);
                } else {
                    // No generated keys found
                    System.out.println("Failed to retrieve generated keys after adding labWork");
                    return -1;
                }
            } else {
                System.out.println("No rows were affected while adding labWork");
                return -1;
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Adds a collection of labWorks to the database.
     *
     * @param labWorks The collection of labWorks to be added.
     * @param userId  The ID of the user adding the labWorks.
     */
    public void addLabWorks(Collection<LabWork> labWorks, int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_LABWORK_SQL)) {
            for (LabWork labWork : labWorks) {
                set(statement, labWork, userId);
                statement.addBatch();
            }
            int[] results = statement.executeBatch();
            // Check the results array to determine the success of each insertion
            for (int result : results) {
                if (result <= 0) {
                    return; // At least one insertion failed
                }
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void set(PreparedStatement statement, LabWork labWork, int userId) throws SQLException {
        statement.setString(1, labWork.getName());
        statement.setDouble(2, labWork.getCoordinates().getX());
        statement.setDouble(3, labWork.getCoordinates().getY());
        statement.setDate(4, Date.valueOf(labWork.getCreationDate()));
        statement.setDouble(5, labWork.getMinimalPoint());
        statement.setInt(6, labWork.getAveragePoint());
        statement.setString(7, labWork.getDifficulty().toString());
        statement.setString(8, labWork.getAuthor().getName());
        statement.setTimestamp(9, Timestamp.valueOf(labWork.getAuthor().getBirthday()));
        statement.setInt(10, labWork.getAuthor().getHeight());
        statement.setDouble(11, labWork.getAuthor().getLocation().getX());
        statement.setLong(12, labWork.getAuthor().getLocation().getY());
        statement.setDouble(13, labWork.getAuthor().getLocation().getZ());
        statement.setString(14, labWork.getAuthor().getLocation().getName());
        statement.setInt(15, userId);

    }

    /**
     * Retrieves all labWorks from the database.
     *
     * @return A list of all labWorks retrieved from the database.
     */
    public List<LabWork> getAllLabWorks() {
        List<LabWork> labWorks = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_LABWORKS_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                LabWork labWork = extractLabWorkFromResultSet(resultSet);
                labWorks.add(labWork);
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return labWorks;
    }

    /**
     * Removes a labWork from the database by its ID.
     *
     * @param labWorkId The ID of the labWork to be removed.
     * @return true if the labWork was successfully removed, false otherwise.
     */
    public boolean removeLabWorkById(int labWorkId) {
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(REMOVE_LABWORKS_SQL)) {
            statement.setInt(1, labWorkId);
            return executePrepareUpdate(statement) > 0;
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a labWork in the database.
     *
     * @param labWork The labWork with updated information.
     * @return true if the labWork was successfully updated, false otherwise.
     */
    public boolean updateLabWork(LabWork labWork, int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_LABWORK_SQL)) {
            set(statement, labWork, userId);
            statement.setInt(16, labWork.getId());
            System.out.println("EXECUTED STATEMENT");
            System.out.println(statement);
            return executePrepareUpdate(statement) > 0;
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates the labWorks table in the database if it does not already exist.
     */
    public void createTableIfNotExist() {
        Connection connection = getConnection();
        System.out.println(CREATE_LABWORKS_TABLE_SQL);
        executeUpdate(connection, CREATE_LABWORKS_TABLE_SQL);
    }

    private LabWork extractLabWorkFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        double coordinatesX = resultSet.getDouble("coordinates_x");
        double coordinatesY = resultSet.getFloat("coordinates_y");
        Date creationDateDate = resultSet.getDate("creation_date");
        LocalDate creationDate = creationDateDate.toLocalDate();
        double minimalPoint = resultSet.getDouble("minimalPoint");
        int averagePoint = resultSet.getInt("averagePoint");
        Difficulty difficulty = Difficulty.valueOf(resultSet.getString("difficulty"));
        String person_name = resultSet.getString("person_name");
        LocalDateTime person_birthday = resultSet.getTimestamp("person_birthday").toLocalDateTime();
        int person_height = resultSet.getInt("person_height");
        double person_location_x = resultSet.getDouble("person_location_x");
        long person_location_y = resultSet.getLong("person_location_y");
        double person_location_z = resultSet.getDouble("person_location_z");

        String person_location_name = resultSet.getString("person_location_name");

        int userId = resultSet.getInt("user_id");
        // Assuming LabWork constructor accepts all these parameters
        LabWork labWork = new LabWork(id, name, new Coordinates(coordinatesX, coordinatesY), creationDate, minimalPoint, averagePoint, difficulty, new Person(person_name, person_birthday, person_height,
                new Location(person_location_x, person_location_y, person_location_z, person_location_name)));
        labWork.setUserId(userId);
        return labWork;
    }

//    /**
//     * Checks if a labWork belongs to a specific user.
//     *
//     * @param labWorkId The ID of the labWork.
//     * @param userId   The ID of the user.
//     * @return true if the labWork belongs to the user, false otherwise.
//     */
//    @Override
//    public boolean checkOwnership(int labWorkId, int userId) {
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement(CHECK_LABWORK_OWNERSHIP_SQL)) {
//            statement.setInt(1, labWorkId);
//            ResultSet resultSet = statement.executeQuery();
//
//            if (resultSet.next()) {
//                int ownerId = resultSet.getInt("user_id");
//                return ownerId == userId;
//            } else {
//                return false;
//            }
//        } catch (NullPointerException exception) {
//            LOGGER.error("Null pointer exception while checking ownership of labWork with ID {}: {}",
//                    labWorkId, exception.getMessage());
//            return false;
//        } catch (SQLException e) {
//            LOGGER.error("Error while checking ownership of labWork with ID {}: {}", labWorkId, e.getMessage());
//            return false;
//        }
//    }


    public boolean removeLabWorksByUserId(int userId) {
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(REMOVE_LABWORKS_BY_USER_ID_SQL)) {
            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeLabWorkByDifficulty(Difficulty difficulty) {
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(REMOVE_LABWORKS_BY_DIFFICULTY_SQL)) {
            statement.setString(1, difficulty.toString());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeGreater(LabWork labWork) {
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(REMOVE_GREATER_BY_AVERAGE_POINT_SQL)) {
            statement.setInt(1, labWork.getAveragePoint());
            System.out.println("EXECUTED STATEMENT");
            System.out.println(statement);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeLower(LabWork labWork) {
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(REMOVE_LOWER_BY_AVERAGE_POINT_SQL)) {
            statement.setInt(1, labWork.getAveragePoint());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}