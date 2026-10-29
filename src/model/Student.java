package model;

/**
 * Student - represents a row in the 'students' table
 */
public class Student {
    private int studentId;
    private String rollNumber;
    private String name;
    private String email;
    private String department;
    private int yearOfStudy;
    private String password;

    // Empty constructor
    public Student() {}

    // Constructor without ID (for new registrations)
    public Student(String rollNumber, String name, String email,
                   String department, int yearOfStudy, String password) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.email = email;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.password = password;
    }

    // Full constructor
    public Student(int studentId, String rollNumber, String name, String email,
                   String department, int yearOfStudy, String password) {
        this.studentId = studentId;
        this.rollNumber = rollNumber;
        this.name = name;
        this.email = email;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.password = password;
    }

    // Getters
    public int getStudentId()      { return studentId; }
    public String getRollNumber()  { return rollNumber; }
    public String getName()        { return name; }
    public String getEmail()       { return email; }
    public String getDepartment()  { return department; }
    public int getYearOfStudy()    { return yearOfStudy; }
    public String getPassword()    { return password; }

    // Setters
    public void setStudentId(int studentId)          { this.studentId = studentId; }
    public void setRollNumber(String rollNumber)     { this.rollNumber = rollNumber; }
    public void setName(String name)                 { this.name = name; }
    public void setEmail(String email)               { this.email = email; }
    public void setDepartment(String department)     { this.department = department; }
    public void setYearOfStudy(int yearOfStudy)      { this.yearOfStudy = yearOfStudy; }
    public void setPassword(String password)         { this.password = password; }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + studentId +
                ", roll='" + rollNumber + '\'' +
                ", name='" + name + '\'' +
                ", dept='" + department + '\'' +
                ", year=" + yearOfStudy +
                '}';
    }
}