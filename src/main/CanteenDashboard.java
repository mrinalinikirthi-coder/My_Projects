
package main;

import model.Student;
import java.util.Scanner;

/**
 * CanteenDashboard - shown after tapping "I'm at Canteen".
 * (Full version in Batch 6)
 */
public class CanteenDashboard {

    private final Scanner sc;
    private final Student student;
    private final int sessionId;

    public CanteenDashboard(Scanner sc, Student student, int sessionId) {
        this.sc = sc;
        this.student = student;
        this.sessionId = sessionId;
    }

    public void show() {
        System.out.println("\n===== 🍽️  CANTEEN DASHBOARD =====");
        System.out.println("Hi " + student.getName() + "!");
        System.out.println("Session #" + sessionId + " started.");
        System.out.println("(Full dashboard coming in Batch 6)");
        System.out.print("Press Enter to return...");
        sc.nextLine();
    }
}