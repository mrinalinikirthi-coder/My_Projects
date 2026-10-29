package main;

import dao.StudentDAO;
import model.Student;

import java.util.Scanner;

/**
 * Test file - run this to verify StudentDAO works
 */
public class TestStudentDAO {

    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== STUDENT DAO TEST =====");
            System.out.println("1. Register new student");
            System.out.println("2. Login");
            System.out.println("3. Get student by ID");
            System.out.println("4. Exit");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Roll Number: ");
                    String roll = sc.nextLine();
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    System.out.print("Department: ");
                    String dept = sc.nextLine();
                    System.out.print("Year: ");
                    int year = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Password: ");
                    String pass = sc.nextLine();

                    Student s = new Student(roll, name, email, dept, year, pass);
                    dao.register(s);
                }
                case 2 -> {
                    System.out.print("Roll Number: ");
                    String roll = sc.nextLine();
                    System.out.print("Password: ");
                    String pass = sc.nextLine();
                    Student s = dao.login(roll, pass);
                    if (s != null) System.out.println("   → " + s);
                }
                case 3 -> {
                    System.out.print("Student ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    Student s = dao.getById(id);
                    if (s != null) System.out.println("   → " + s);
                    else System.out.println("   Not found.");
                }
                case 4 -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
            sc.close();
        }
    }
}