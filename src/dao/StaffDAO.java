package dao;

import db.DBConnection;
import model.Staff;
import java.sql.*;

/**
 * StaffDAO - handles all database operations for staff
 */
public class StaffDAO {

    /**
     * Login using PIN. Returns Staff object if valid, null otherwise.
     */
    public Staff loginByPin(String pin) {
        String sql = "SELECT * FROM staff WHERE login_pin = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pin);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Staff s = mapRow(rs);
                System.out.println("✅ Welcome, " + s.getName() + " (" + s.getRole() + ")!");
                return s;
            } else {
                System.out.println("❌ Invalid PIN.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("❌ Staff login error: " + e.getMessage());
            return null;
        }
    }

    /** Get staff by ID */
    public Staff getById(int staffId) {
        String sql = "SELECT * FROM staff WHERE staff_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("❌ StaffDAO getById error: " + e.getMessage());
        }
        return null;
    }

    private Staff mapRow(ResultSet rs) throws SQLException {
        return new Staff(
                rs.getInt("staff_id"),
                rs.getString("name"),
                rs.getString("role"),
                rs.getString("login_pin")
        );
    }
}