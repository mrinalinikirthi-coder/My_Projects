package main;

import dao.StaffDAO;
import dao.StudentDAO;
import model.Staff;
import model.Student;

import java.util.Scanner;

/**
 * LoginScreen - handles both student and staff login/register.
 */
public class LoginScreen {

    private final Scanner sc;
    private final StudentDAO studentDAO = new StudentDAO();
    private final StaffDAO staffDAO = new StaffDAO();

    public LoginScreen(Scanner sc) {
        this.sc = sc;
    }

    // =====================================================
    // STUDENT LOGIN
    // =====================================================
    public void showStudentLogin() {
        while (true) {
            System.out.println("\n===== 🎓 STUDENT LOGIN =====");
            System.out.println("1. Login");
            System.out.println("2. Register new account");
            System.out.println("3. Back");
            System.out.print("Choice: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    Student s = doStudentLogin();
                    if (s != null) {
                        new HomeScreen(sc, s).show();
                        return;
                    }
                }
                case "2" -> doStudentRegister();
                case "3" -> { return; }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    private Student doStudentLogin() {
        System.out.print("Roll Number: ");
        String roll = sc.nextLine().trim();
        System.out.print("Password: ");
        String pass = sc.nextLine().trim();
        return studentDAO.login(roll, pass);
    }

    private void doStudentRegister() {
        System.out.println("\n--- 📝 New Student Registration ---");
        System.out.print("Roll Number: ");
        String roll = sc.nextLine().trim();
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Department (CSE/ECE/...): ");
        String dept = sc.nextLine().trim().toUpperCase();
        System.out.print("Year (1-4): ");
        int year;
        try {
            year = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid year.");
            return;
        }
        System.out.print("Password: ");
        String pass = sc.nextLine().trim();

        Student s = new Student(roll, name, email, dept, year, pass);
        int id = studentDAO.register(s);
        if (id > 0) {
            System.out.println("✅ Registration successful! Please login.");
        }
    }

    // =====================================================
    // STAFF LOGIN
    // =====================================================
    public void showStaffLogin() {
        System.out.println("\n===== 👨‍🍳 STAFF LOGIN =====");
        System.out.println("💡 Demo PINs: 1234 (Cashier), 5678 (Cook)");
        System.out.print("Enter PIN: ");
        String pin = sc.nextLine().trim();

        Staff staff = staffDAO.loginByPin(pin);
        if (staff == null) return;

        if (staff.isCashier()) {
            new CashierScreen(sc, staff).show();
        } else if (staff.isCook()) {
            new KitchenDisplay(sc, staff).show();
        } else {
            System.out.println("⚠️  Role not supported yet: " + staff.getRole());
        }
    }
}