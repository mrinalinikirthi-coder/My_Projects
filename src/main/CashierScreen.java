package main;

import model.Staff;
import java.util.Scanner;

/**
 * CashierScreen - cashier's pending orders + billing.
 * (Full version in Batch 6)
 */
public class CashierScreen {

    private final Scanner sc;
    private final Staff staff;

    public CashierScreen(Scanner sc, Staff staff) {
        this.sc = sc;
        this.staff = staff;
    }

    public void show() {
        System.out.println("\n===== 💰 CASHIER SCREEN =====");
        System.out.println("Logged in as: " + staff.getName());
        System.out.println("(Full cashier screen coming in Batch 6)");
        System.out.print("Press Enter to return...");
        sc.nextLine();
    }
}