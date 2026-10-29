package main;

import dao.CanteenSessionDAO;
import dao.NotificationDAO;
import model.CanteenSession;
import model.Student;

import java.util.List;
import java.util.Scanner;

/**
 * HomeScreen - the 2-button screen students see after login.
 */
public class HomeScreen {

    private final Scanner sc;
    private final Student student;
    private final CanteenSessionDAO sessionDAO = new CanteenSessionDAO();
    private final NotificationDAO notifDAO = new NotificationDAO();

    public HomeScreen(Scanner sc, Student student) {
        this.sc = sc;
        this.student = student;
    }

    public void show() {
        while (true) {
            printHeader();

            System.out.println("1. 📍 I'm at Canteen");
            System.out.println("2. 📊 Check Queue Status");
            System.out.println("3. 🔔 My Notifications");
            System.out.println("4. ⏱️  My Wait History");
            System.out.println("5. 🚪 Logout");
            System.out.print("\nChoice: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> handleEnterCanteen();
                case "2" -> showQueueStatus();
                case "3" -> showNotifications();
                case "4" -> showWaitHistory();
                case "5" -> {
                    System.out.println("👋 Logged out.");
                    return;
                }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void printHeader() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.printf("║  Welcome, %-27s║%n", student.getName());
        System.out.printf("║  Roll: %-31s║%n", student.getRollNumber());
        System.out.println("╚══════════════════════════════════════╝");
    }

    // =====================================================
    // Option 1: I'm at Canteen
    // =====================================================
    private void handleEnterCanteen() {
        int sessionId = sessionDAO.startSession(student.getStudentId());
        if (sessionId == -1) {
            System.out.println("❌ Failed to start session.");
            return;
        }
        new CanteenDashboard(sc, student, sessionId).show();
    }

    // =====================================================
    // Option 2: Check Queue Status
    // =====================================================
    private void showQueueStatus() {
        int queue = sessionDAO.getCurrentQueueCount();
        double avgWait = sessionDAO.getAvgWaitLastHour();

        String status, emoji;
        if (queue <= 3)       { status = "FREE";     emoji = "🟢"; }
        else if (queue <= 8)  { status = "MODERATE"; emoji = "🟡"; }
        else                  { status = "RUSH";     emoji = "🔴"; }

        System.out.println("\n========== 📊 LIVE QUEUE STATUS ==========");
        System.out.println("👥 People inside: " + queue);
        System.out.printf("⏱️  Avg wait (last hour): %.1f min%n", avgWait);
        System.out.println("📈 Live status: " + emoji + " " + status);
        System.out.println("==========================================");
        System.out.print("\nPress Enter to return...");
        sc.nextLine();
    }

    // =====================================================
    // Option 3: Notifications
    // =====================================================
    private void showNotifications() {
        List<model.Notification> list = notifDAO.getAll(student.getStudentId());

        System.out.println("\n========== 🔔 MY NOTIFICATIONS ==========");
        if (list.isEmpty()) {
            System.out.println("(No notifications yet)");
        } else {
            for (model.Notification n : list) {
                System.out.println("   " + n);
            }
            notifDAO.markAllRead(student.getStudentId());
        }
        System.out.println("=========================================");
        System.out.print("\nPress Enter to return...");
        sc.nextLine();
    }

    // =====================================================
    // Option 4: Wait History
    // =====================================================
    private void showWaitHistory() {
        List<CanteenSession> list = sessionDAO.getHistoryForStudent(student.getStudentId());

        System.out.println("\n========== ⏱️  MY WAIT HISTORY ==========");
        if (list.isEmpty()) {
            System.out.println("(No past visits yet)");
        } else {
            for (CanteenSession s : list) {
                System.out.printf("   #%d | %s | wait: %s min%n",
                        s.getSessionId(),
                        s.getEntryTime(),
                        s.getWaitMinutes() != null ? s.getWaitMinutes() : "?");
            }
        }
        System.out.println("========================================");
        System.out.print("\nPress Enter to return...");
        sc.nextLine();
    }
}