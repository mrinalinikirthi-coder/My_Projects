package dao;

import db.DBConnection;
import model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * NotificationDAO - handles student notifications.
 * (Staff don't get notifications — they use queue screens)
 */
public class NotificationDAO {

    /** Insert a new notification for a student */
    public void create(int studentId, String message) {
        String sql = "INSERT INTO notifications (student_id, message) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, message);
            ps.executeUpdate();
            System.out.println("🔔 Notification sent to student #" + studentId);
        } catch (SQLException e) {
            System.out.println("❌ NotificationDAO create error: " + e.getMessage());
        }
    }

    /** Get all unread notifications for a student */
    public List<Notification> getUnread(int studentId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications " +
                "WHERE student_id = ? AND is_read = FALSE " +
                "ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("❌ NotificationDAO getUnread error: " + e.getMessage());
        }
        return list;
    }

    /** Get all notifications (read + unread) for a student */
    public List<Notification> getAll(int studentId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE student_id = ? " +
                "ORDER BY created_at DESC LIMIT 20";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("❌ NotificationDAO getAll error: " + e.getMessage());
        }
        return list;
    }

    /** Mark all notifications for a student as read */
    public void markAllRead(int studentId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ markAllRead error: " + e.getMessage());
        }
    }

    private Notification mapRow(ResultSet rs) throws SQLException {
        return new Notification(
                rs.getInt("notification_id"),
                rs.getInt("student_id"),
                rs.getString("message"),
                rs.getBoolean("is_read"),
                rs.getTimestamp("created_at")
        );
    }
}