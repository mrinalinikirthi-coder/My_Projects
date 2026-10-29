package model;

import java.sql.Timestamp;

/**
 * Notification - represents a row in the 'notifications' table
 * (Only for students — staff use queue screens)
 */
public class Notification {
    private int notificationId;
    private int studentId;
    private String message;
    private boolean isRead;
    private Timestamp createdAt;

    public Notification() {}

    public Notification(int studentId, String message) {
        this.studentId = studentId;
        this.message = message;
        this.isRead = false;
    }

    public Notification(int notificationId, int studentId, String message,
                        boolean isRead, Timestamp createdAt) {
        this.notificationId = notificationId;
        this.studentId = studentId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters
    public int getNotificationId()   { return notificationId; }
    public int getStudentId()        { return studentId; }
    public String getMessage()       { return message; }
    public boolean isRead()          { return isRead; }
    public Timestamp getCreatedAt()  { return createdAt; }

    // Setters
    public void setNotificationId(int notificationId)   { this.notificationId = notificationId; }
    public void setStudentId(int studentId)             { this.studentId = studentId; }
    public void setMessage(String message)              { this.message = message; }
    public void setRead(boolean read)                   { this.isRead = read; }
    public void setCreatedAt(Timestamp createdAt)       { this.createdAt = createdAt; }

    @Override
    public String toString() {
        String readMark = isRead ? "  " : "🔵";  // 🔵 = unread
        return String.format("%s [%s] %s", readMark, createdAt, message);
    }
}