package main;

import model.Staff;
import java.util.Scanner;

/**
 * KitchenDisplay - cook's FIFO order queue.
 * (Full version in Batch 6)
 */
public class KitchenDisplay {

    private final Scanner sc;
    private final Staff staff;

    public KitchenDisplay(Scanner sc, Staff staff) {
        this.sc = sc;
        this.staff = staff;
    }

    public void show() {
        System.out.println("\n===== 🍳 KITCHEN DISPLAY =====");
        System.out.println("Logged in as: " + staff.getName());
        System.out.println("(Full kitchen display coming in Batch 6)");
        System.out.print("Press Enter to return...");
        sc.nextLine();
    }
}