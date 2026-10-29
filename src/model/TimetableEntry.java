package model;

import java.sql.Time;

/**
 * TimetableEntry - represents a row in the 'timetable' table
 * Each row = one class in a student's weekly schedule
 */
public class TimetableEntry {
    private int timetableId;
    private int studentId;
    private String subjectName;
    private String dayOfWeek;   // MON/TUE/WED/THU/FRI/SAT
    private Time startTime;
    private Time endTime;

    public TimetableEntry() {}

    public TimetableEntry(int studentId, String subjectName, String dayOfWeek,
                          Time startTime, Time endTime) {
        this.studentId = studentId;
        this.subjectName = subjectName;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TimetableEntry(int timetableId, int studentId, String subjectName,
                          String dayOfWeek, Time startTime, Time endTime) {
        this.timetableId = timetableId;
        this.studentId = studentId;
        this.subjectName = subjectName;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public int getTimetableId()     { return timetableId; }
    public int getStudentId()       { return studentId; }
    public String getSubjectName()  { return subjectName; }
    public String getDayOfWeek()    { return dayOfWeek; }
    public Time getStartTime()      { return startTime; }
    public Time getEndTime()        { return endTime; }

    // Setters
    public void setTimetableId(int timetableId)         { this.timetableId = timetableId; }
    public void setStudentId(int studentId)             { this.studentId = studentId; }
    public void setSubjectName(String subjectName)      { this.subjectName = subjectName; }
    public void setDayOfWeek(String dayOfWeek)          { this.dayOfWeek = dayOfWeek; }
    public void setStartTime(Time startTime)            { this.startTime = startTime; }
    public void setEndTime(Time endTime)                { this.endTime = endTime; }

    @Override
    public String toString() {
        return String.format("%s | %-20s | %s - %s",
                dayOfWeek, subjectName, startTime, endTime);
    }
}