package dao;

import db.DBConnection;
import model.Student;

import java.sql.*;

/**
 * StudentDAO - handles all database operations for students
 * DAO = Data Access Object
 */
public class StudentDAO {

    /**
     * Register a new student.
     * Returns the generated student_id, or -1 if failed.
     */
    public int register(Student s) {
        String sql = "INSERT INTO students " +
                     "(roll_number, name, email, department, year_of_study, password) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getRollNumber());
            ps.setString(2, s.getName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getDepartment());
            ps.setInt(5, s.getYearOfStudy());
            ps.setString(6, s.getPassword());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    s.setStudentId(newId);
                    System.out.println("✅ Student registered with ID: " + newId);
                    return newId;
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.out.println("❌ Roll number or email already exists!");
            } else {
                System.out.println("❌ Registration failed: " + e.getMessage());
            }
        }
        return -1;
    }

    /**
     * Login - verify roll number + password
     * Returns Student object if valid, null otherwise.
     */
    public Student login(String rollNumber, String password) {
        String sql = "SELECT * FROM students WHERE roll_number = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rollNumber);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Student s = mapRowToStudent(rs);
                System.out.println("✅ Welcome, " + s.getName() + "!");
                return s;
            } else {
                System.out.println("❌ Invalid roll number or password.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("❌ Login error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get a student by their ID.
     */
    public Student getById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapRowToStudent(rs);
        } catch (SQLException e) {
            System.out.println("❌ getById error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Helper: Convert current ResultSet row → Student object
     */
    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("student_id"),
                rs.getString("roll_number"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("department"),
                rs.getInt("year_of_study"),
                rs.getString("password")
        );
    }
}