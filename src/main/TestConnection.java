package main;

import db.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * TestConnection - Verifies JDBC connection + reads sample data
 */
public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("✅ Connected to CanteenFlow database!\n");

            // Test 1: Count students
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS c FROM students");
            rs.next();
            System.out.println("👥 Total students: " + rs.getInt("c"));

            // Test 2: Show menu items
            rs = stmt.executeQuery("SELECT item_name, price FROM menu_items");
            System.out.println("\n🍔 Menu Items:");
            while (rs.next()) {
                System.out.printf("   %-15s ₹%.2f%n",
                        rs.getString("item_name"),
                        rs.getDouble("price"));
            }

            // Test 3: Sample prediction query
            rs = stmt.executeQuery(
                "SELECT hour_of_day, AVG(wait_minutes) AS avg_wait " +
                "FROM canteen_sessions " +
                "WHERE served_time IS NOT NULL " +
                "GROUP BY hour_of_day"
            );
            System.out.println("\n📊 Queue pattern (will fill up once we log sessions):");
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.println("   Hour " + rs.getInt("hour_of_day") +
                        " → avg wait " + rs.getDouble("avg_wait") + " min");
            }
            if (!hasData) System.out.println("   (No session data yet — that's fine!)");

            System.out.println("\n🎉 JDBC setup is working! Ready to code.");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}