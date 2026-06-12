import model.User;
import utility.DatabaseConnectionManager;
import utility.DatabaseManager;
import utility.LabWorkDAO;
import utility.UserDAO;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection connection = DatabaseConnectionManager.getConnection();
        UserDAO dao = new UserDAO();
        dao.insertUser("Admin", "admin");
        System.out.println(connection);
    }
}
