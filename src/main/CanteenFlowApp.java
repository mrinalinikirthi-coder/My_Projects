package main;

import java.util.Scanner;

/**
 * CanteenFlowApp - the main entry point.
 * Shows welcome banner, then routes to student/staff login.
 */
public class CanteenFlowApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            printBanner();

            System.out.println("1. 🎓 Student Login");
            System.out.println("2. 👨‍🍳 Staff Login (Cashier / Cook)");
            System.out.println("3. Exit");
            System.out.print("\nChoice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input.");
                continue;
            }

            switch (choice) {
                case 1 -> new LoginScreen(sc).showStudentLogin();
                case 2 -> new LoginScreen(sc).showStaffLogin();
                case 3 -> {
                    System.out.println("\n👋 Thanks for using CanteenFlow!");
                    sc.close();
                    return;
                }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    private static void printBanner() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║        🍽️   CANTEENFLOW   🍽️         ║");
        System.out.println("║   Smart Canteen Queue & Ordering    ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
}   