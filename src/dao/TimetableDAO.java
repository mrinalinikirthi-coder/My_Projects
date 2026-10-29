package dao;

import db.DBConnection;
import model.TimetableEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TimetableDAO - manages student class schedules.
 */
public class TimetableDAO {

    /** Get all classes for a student on a given day */
    public List<TimetableEntry> getByDay(int studentId, String dayOfWeek) {
        List<TimetableEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM timetable WHERE student_id = ? AND day_of_week = ? " +
                "ORDER BY start_time";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, dayOfWeek);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("❌ TimetableDAO getByDay error: " + e.getMessage());
        }
        return list;
    }

    /** Get all classes for a student (all days) */
    public List<TimetableEntry> getAll(int studentId) {
        List<TimetableEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM timetable WHERE student_id = ? " +
                "ORDER BY FIELD(day_of_week,'MON','TUE','WED','THU','FRI','SAT'), start_time";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("❌ TimetableDAO getAll error: " + e.getMessage());
        }
        return list;
    }

    /** Add a new class to student's timetable */
    public boolean add(TimetableEntry t) {
        String sql = "INSERT INTO timetable " +
                "(student_id, subject_name, day_of_week, start_time, end_time) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getStudentId());
            ps.setString(2, t.getSubjectName());
            ps.setString(3, t.getDayOfWeek());
            ps.setTime(4, t.getStartTime());
            ps.setTime(5, t.getEndTime());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Class added: " + t.getSubjectName());
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ TimetableDAO add error: " + e.getMessage());
        }
        return false;
    }

    private TimetableEntry mapRow(ResultSet rs) throws SQLException {
        return new TimetableEntry(
                rs.getInt("timetable_id"),
                rs.getInt("student_id"),
                rs.getString("subject_name"),
                rs.getString("day_of_week"),
                rs.getTime("start_time"),
                rs.getTime("end_time")
        );
    }
}