package studentmanagementsystemm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections.
 */
public class DatabaseConnection {

    // The connection string for the database
    static final String URL = "jdbc:mysql://127.0.0.1:3306/student_mgmt_v2";
    static final String USER = "root";
    static final String PASSWORD = "";

    /**
     * Returns a new connection to the database.
     * 
     * @return Connection object
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("❌ Database connection failed! " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("❌ MySQL JDBC Driver not found!");
        }
        return connection;
    }

    /**
     * Main method for testing the connection.
     */
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("✅ Connection successful!");
            try {
                // Perform any database operations here
            } finally {
                try {
                    conn.close();
                    System.out.println("✅ Connection closed!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("❌ Error closing the connection!");
                }
            }
        } else {
            System.out.println("❌ Connection failed!");
        }
    }
}
