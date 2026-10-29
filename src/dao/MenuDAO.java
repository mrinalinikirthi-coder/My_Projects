package dao;

import db.DBConnection;
import model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MenuDAO - all database operations for menu items
 */
public class MenuDAO {

    /** Fetch all available menu items */
    public List<MenuItem> getAllItems() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE is_available = TRUE ORDER BY category, item_name";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("❌ MenuDAO error: " + e.getMessage());
        }
        return list;
    }

    /** Fetch single item by ID (useful for price lookup) */
    public MenuItem getById(int itemId) {
        String sql = "SELECT * FROM menu_items WHERE item_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("❌ MenuDAO getById error: " + e.getMessage());
        }
        return null;
    }

    /** Print menu in a nice format */
    public void printMenu() {
        List<MenuItem> items = getAllItems();
        System.out.println("\n========== 🍔 MENU ==========");
        String currentCategory = "";
        for (MenuItem item : items) {
            if (!item.getCategory().equals(currentCategory)) {
                currentCategory = item.getCategory();
                System.out.println("\n-- " + currentCategory + " --");
            }
            System.out.println("  " + item);
        }
        System.out.println("==============================");
    }

    private MenuItem mapRow(ResultSet rs) throws SQLException {
        return new MenuItem(
                rs.getInt("item_id"),
                rs.getString("item_name"),
                rs.getDouble("price"),
                rs.getString("category"),
                rs.getInt("avg_prep_time_min"),
                rs.getBoolean("is_available")
        );
    }
}