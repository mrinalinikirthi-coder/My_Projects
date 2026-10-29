package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Single place to get MySQL connection
 * Change USER and PASSWORD to match your MySQL setup
 */
public class DBConnection {

    // 👇 CHANGE THESE TWO LINES TO MATCH YOUR MySQL CREDENTIALS
    private static final String USER = "root";
    private static final String PASSWORD = "12345";   // ← your MySQL root password

   private static final String URL = 
    "jdbc:mysql://localhost:3306/canteenflow?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true";
    /**
     * Returns a fresh connection to the canteenflow database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Quick test method - run this file directly to verify connection.
     */
    public static void main(String[] args) {
        System.out.println("⏳ Trying to connect to MySQL...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connected successfully to canteenflow database!");
                System.out.println("   Database: " + conn.getCatalog());
                System.out.println("   MySQL version: " + conn.getMetaData().getDatabaseProductVersion());
            }
        } catch (SQLException e) {
            System.out.println("❌ Connection FAILED!");
            System.out.println("   Error: " + e.getMessage());
            System.out.println("\n👉 Check:");
            System.out.println("   1. Is MySQL Server running?");
            System.out.println("   2. Did you change USER/PASSWORD in DBConnection.java?");
            System.out.println("   3. Did you run schema.sql in MySQL Workbench?");
        }
    }
}