package dao;

import db.DBConnection;
import model.CanteenSession;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * CanteenSessionDAO - the heart of CanteenFlow.
 * Handles: starting sessions, ending sessions, current queue, avg wait, student history.
 */
public class CanteenSessionDAO {

    /**
     * Check if a student currently has an active session
     * (status IN_QUEUE or ORDERED).
     */
    public boolean hasActiveSession(int studentId) {
        String sql = "SELECT 1 FROM canteen_sessions " +
                     "WHERE student_id = ? AND status IN ('IN_QUEUE','ORDERED') LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("❌ hasActiveSession error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the active session ID for a student (or -1 if none).
     */
    public int getActiveSessionId(int studentId) {
        String sql = "SELECT session_id FROM canteen_sessions " +
                     "WHERE student_id = ? AND status IN ('IN_QUEUE','ORDERED') " +
                     "ORDER BY entry_time DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("session_id");
        } catch (SQLException e) {
            System.out.println("❌ getActiveSessionId error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Get the full active session object for a student.
     * Returns null if no active session.
     */
    public CanteenSession getCurrentSession(int studentId) {
        int sessionId = getActiveSessionId(studentId);
        if (sessionId == -1) return null;
        return getById(sessionId);
    }

    /**
     * Start a new session when a student taps "I'm at Canteen".
     * Returns the new session_id, or -1 on failure.
     */
    public int startSession(int studentId) {
        if (hasActiveSession(studentId)) {
            System.out.println("⚠️  You already have an active session!");
            return getActiveSessionId(studentId);
        }

        LocalDateTime now = LocalDateTime.now();
        String day = now.format(DateTimeFormatter.ofPattern("EEE")).toUpperCase();
        int hour = now.getHour();

        String sql = "INSERT INTO canteen_sessions " +
                     "(student_id, entry_time, status, day_of_week, hour_of_day) " +
                     "VALUES (?, NOW(), 'IN_QUEUE', ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, studentId);
            ps.setString(2, day);
            ps.setInt(3, hour);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int sessionId = keys.getInt(1);
                    System.out.println("✅ Session started! ID: " + sessionId +
                            " at " + now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    return sessionId;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ startSession error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Staff marks a session as SERVED.
     * Also updates the linked order to SERVED (if any).
     * Uses a transaction for atomicity.
     */
    public boolean markServed(int sessionId) {
        String updateSession = 
            "UPDATE canteen_sessions " +
            "SET served_time = NOW(), " +
            "    wait_minutes = TIMESTAMPDIFF(MINUTE, entry_time, NOW()), " +
            "    status = 'SERVED' " +
            "WHERE session_id = ? AND status IN ('IN_QUEUE','ORDERED')";

        String updateOrder = 
            "UPDATE orders SET status = 'SERVED', served_at = NOW() " +
            "WHERE session_id = ? AND status IN ('PLACED','PREPARING','READY')";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updateSession)) {
                ps.setInt(1, sessionId);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    System.out.println("⚠️  No active session #" + sessionId + " to mark served.");
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(updateOrder)) {
                ps.setInt(1, sessionId);
                int orderRows = ps.executeUpdate();
                if (orderRows > 0) {
                    System.out.println("   → Linked order also marked SERVED.");
                }
            }

            conn.commit();

            int wait = getWaitMinutes(sessionId);
            System.out.println("✅ Session #" + sessionId +
                    " marked SERVED. Wait time: " + wait + " min.");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ markServed error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignore) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignore) {}
            }
        }
    }

    /** Get the wait_minutes for a session */
    public int getWaitMinutes(int sessionId) {
        String sql = "SELECT wait_minutes FROM canteen_sessions WHERE session_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("wait_minutes");
        } catch (SQLException e) {
            System.out.println("❌ getWaitMinutes error: " + e.getMessage());
        }
        return -1;
    }

    /** How many people are in the canteen RIGHT NOW? */
    public int getCurrentQueueCount() {
        String sql = "SELECT COUNT(*) AS c FROM canteen_sessions " +
                     "WHERE status IN ('IN_QUEUE','ORDERED') " +
                     "AND DATE(entry_time) = CURDATE()";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("c");
        } catch (SQLException e) {
            System.out.println("❌ getCurrentQueueCount error: " + e.getMessage());
        }
        return 0;
    }

    /** Average wait time (in minutes) over the last 1 hour */
    public double getAvgWaitLastHour() {
        String sql = "SELECT AVG(wait_minutes) AS avg_wait FROM canteen_sessions " +
                     "WHERE status = 'SERVED' AND served_time >= NOW() - INTERVAL 1 HOUR";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                double avg = rs.getDouble("avg_wait");
                return rs.wasNull() ? 0.0 : avg;
            }
        } catch (SQLException e) {
            System.out.println("❌ getAvgWaitLastHour error: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Get a student's past sessions (last 10), with wait times.
     * Only shows SERVED sessions.
     */
    public List<CanteenSession> getHistoryForStudent(int studentId) {
        List<CanteenSession> list = new ArrayList<>();
        String sql = "SELECT * FROM canteen_sessions " +
                     "WHERE student_id = ? AND status = 'SERVED' " +
                     "ORDER BY entry_time DESC LIMIT 10";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CanteenSession cs = new CanteenSession();
                cs.setSessionId(rs.getInt("session_id"));
                cs.setStudentId(rs.getInt("student_id"));
                cs.setEntryTime(rs.getTimestamp("entry_time"));
                cs.setServedTime(rs.getTimestamp("served_time"));
                cs.setWaitMinutes((Integer) rs.getObject("wait_minutes"));
                cs.setStatus(rs.getString("status"));
                cs.setDayOfWeek(rs.getString("day_of_week"));
                cs.setHourOfDay(rs.getInt("hour_of_day"));
                list.add(cs);
            }
        } catch (SQLException e) {
            System.out.println("❌ getHistoryForStudent error: " + e.getMessage());
        }
        return list;
    }

    /** Get a session object by ID */
    public CanteenSession getById(int sessionId) {
        String sql = "SELECT * FROM canteen_sessions WHERE session_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CanteenSession cs = new CanteenSession();
                cs.setSessionId(rs.getInt("session_id"));
                cs.setStudentId(rs.getInt("student_id"));
                cs.setEntryTime(rs.getTimestamp("entry_time"));
                cs.setServedTime(rs.getTimestamp("served_time"));
                cs.setExitTime(rs.getTimestamp("exit_time"));
                cs.setWaitMinutes((Integer) rs.getObject("wait_minutes"));
                cs.setStatus(rs.getString("status"));
                cs.setDayOfWeek(rs.getString("day_of_week"));
                cs.setHourOfDay(rs.getInt("hour_of_day"));
                return cs;
            }
        } catch (SQLException e) {
            System.out.println("❌ getById error: " + e.getMessage());
        }
        return null;
    }
}