package main;

import dao.CanteenSessionDAO;
import model.CanteenSession;
import java.util.List;
import java.util.Scanner;

/**
 * Test CanteenSessionDAO - verify session logic works.
 */
public class TestSessionDAO {

    public static void main(String[] args) {
        CanteenSessionDAO dao = new CanteenSessionDAO();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== SESSION DAO TEST =====");
            System.out.println("1. Start session (student taps 'I'm at Canteen')");
            System.out.println("2. Check if student has active session");
            System.out.println("3. Mark session as SERVED (staff action)");
            System.out.println("4. Show current queue count");
            System.out.println("5. Show avg wait (last hour)");
            System.out.println("6. Show student's wait history");
            System.out.println("7. View a session by ID");
            System.out.println("8. Exit");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Student ID (try 1, 2, or 3): ");
                    int sid = sc.nextInt(); sc.nextLine();
                    dao.startSession(sid);
                }
                case 2 -> {
                    System.out.print("Student ID: ");
                    int sid = sc.nextInt(); sc.nextLine();
                    boolean active = dao.hasActiveSession(sid);
                    if (active) {
                        int sessId = dao.getActiveSessionId(sid);
                        System.out.println("✅ Yes — active session #" + sessId);
                    } else {
                        System.out.println("❌ No active session.");
                    }
                }
                case 3 -> {
                    System.out.print("Session ID: ");
                    int sessId = sc.nextInt(); sc.nextLine();
                    dao.markServed(sessId);
                }
                case 4 -> System.out.println("👥 Current queue: " +
                        dao.getCurrentQueueCount() + " people");
                case 5 -> System.out.printf("⏱️  Avg wait last hour: %.1f min%n",
                        dao.getAvgWaitLastHour());
                case 6 -> {
                    System.out.print("Student ID: ");
                    int sid = sc.nextInt(); sc.nextLine();
                    List<CanteenSession> history = dao.getHistoryForStudent(sid);
                    if (history.isEmpty()) {
                        System.out.println("(No past visits for student #" + sid + ")");
                    } else {
                        System.out.println("📜 Wait history for student #" + sid + ":");
                        for (CanteenSession s : history) {
                            System.out.printf("   #%d | %s | wait: %s min | status: %s%n",
                                    s.getSessionId(),
                                    s.getEntryTime(),
                                    s.getWaitMinutes(),
                                    s.getStatus());
                        }
                    }
                }
                case 7 -> {
                    System.out.print("Session ID: ");
                    int sessId = sc.nextInt(); sc.nextLine();
                    var s = dao.getById(sessId);
                    if (s != null) System.out.println("   → " + s);
                    else System.out.println("   Not found.");
                }
                case 8 -> {
                    System.out.println("Bye!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid.");
            }
        }
    }
}