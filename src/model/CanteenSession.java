package model;

import java.sql.Timestamp;

/**
 * CanteenSession - represents a row in the 'canteen_sessions' table
 * This is the CORE model of our project.
 */
public class CanteenSession {
    private int sessionId;
    private int studentId;
    private Timestamp entryTime;
    private Timestamp servedTime;
    private Timestamp exitTime;
    private Integer waitMinutes;      // can be null (until served)
    private String status;            // IN_QUEUE / ORDERED / SERVED 
    private String dayOfWeek;
    private int hourOfDay;

    public CanteenSession() {}

    // Constructor for starting a NEW session
    public CanteenSession(int studentId, String dayOfWeek, int hourOfDay) {
        this.studentId = studentId;
        this.status = "IN_QUEUE";
        this.dayOfWeek = dayOfWeek;
        this.hourOfDay = hourOfDay;
    }

    // Getters
    public int getSessionId()           { return sessionId; }
    public int getStudentId()           { return studentId; }
    public Timestamp getEntryTime()     { return entryTime; }
    public Timestamp getServedTime()    { return servedTime; }
    public Timestamp getExitTime()      { return exitTime; }
    public Integer getWaitMinutes()     { return waitMinutes; }
    public String getStatus()           { return status; }
    public String getDayOfWeek()        { return dayOfWeek; }
    public int getHourOfDay()           { return hourOfDay; }

    // Setters
    public void setSessionId(int sessionId)            { this.sessionId = sessionId; }
    public void setStudentId(int studentId)            { this.studentId = studentId; }
    public void setEntryTime(Timestamp entryTime)      { this.entryTime = entryTime; }
    public void setServedTime(Timestamp servedTime)    { this.servedTime = servedTime; }
    public void setExitTime(Timestamp exitTime)        { this.exitTime = exitTime; }
    public void setWaitMinutes(Integer waitMinutes)    { this.waitMinutes = waitMinutes; }
    public void setStatus(String status)               { this.status = status; }
    public void setDayOfWeek(String dayOfWeek)         { this.dayOfWeek = dayOfWeek; }
    public void setHourOfDay(int hourOfDay)            { this.hourOfDay = hourOfDay; }

    @Override
    public String toString() {
        return "CanteenSession{" +
                "id=" + sessionId +
                ", studentId=" + studentId +
                ", entry=" + entryTime +
                ", served=" + servedTime +
                ", wait=" + waitMinutes + "min" +
                ", status='" + status + '\'' +
                '}';
    }
}