package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Order - represents a row in the 'orders' table
 * 
 * Status flow:
 *   PLACED     → student submitted, waiting for cashier
 *   PREPARING  → cashier confirmed, cook is preparing
 *   READY      → cook finished, waiting for pickup
 *   SERVED     → handed to student, order complete
 */
public class Order {
    private int orderId;
    private int sessionId;
    private int studentId;
    private String placedBy;              // STUDENT or CASHIER
    private Integer confirmedByStaffId;   // nullable — who confirmed it
    private double totalAmount;
    private Timestamp orderTime;
    private Timestamp confirmedAt;
    private Timestamp readyAt;
    private Timestamp servedAt;
    private String status;                // PLACED / PREPARING / READY / SERVED
    private List<OrderItem> items = new ArrayList<>();

    public Order() {}

    // Constructor for a NEW order (student or cashier placing it)
    public Order(int sessionId, int studentId, String placedBy) {
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.placedBy = placedBy;
        this.status = "PLACED";
    }

    // Getters
    public int getOrderId()                     { return orderId; }
    public int getSessionId()                   { return sessionId; }
    public int getStudentId()                   { return studentId; }
    public String getPlacedBy()                 { return placedBy; }
    public Integer getConfirmedByStaffId()      { return confirmedByStaffId; }
    public double getTotalAmount()              { return totalAmount; }
    public Timestamp getOrderTime()             { return orderTime; }
    public Timestamp getConfirmedAt()           { return confirmedAt; }
    public Timestamp getReadyAt()               { return readyAt; }
    public Timestamp getServedAt()              { return servedAt; }
    public String getStatus()                   { return status; }
    public List<OrderItem> getItems()           { return items; }

    // Setters
    public void setOrderId(int orderId)                         { this.orderId = orderId; }
    public void setSessionId(int sessionId)                     { this.sessionId = sessionId; }
    public void setStudentId(int studentId)                     { this.studentId = studentId; }
    public void setPlacedBy(String placedBy)                    { this.placedBy = placedBy; }
    public void setConfirmedByStaffId(Integer confirmedByStaffId) { this.confirmedByStaffId = confirmedByStaffId; }
    public void setTotalAmount(double totalAmount)              { this.totalAmount = totalAmount; }
    public void setOrderTime(Timestamp orderTime)               { this.orderTime = orderTime; }
    public void setConfirmedAt(Timestamp confirmedAt)           { this.confirmedAt = confirmedAt; }
    public void setReadyAt(Timestamp readyAt)                   { this.readyAt = readyAt; }
    public void setServedAt(Timestamp servedAt)                 { this.servedAt = servedAt; }
    public void setStatus(String status)                        { this.status = status; }
    public void setItems(List<OrderItem> items)                 { this.items = items; }

    public void addItem(OrderItem item) {
        items.add(item);
        recalculateTotal();
    }

    private void recalculateTotal() {
        double total = 0;
        for (OrderItem i : items) total += i.getSubtotal();
        this.totalAmount = total;
    }

    @Override
    public String toString() {
        return String.format("Order #%d | ₹%.2f | %d items | %s | placed by %s",
                orderId, totalAmount, items.size(), status, placedBy);
    }
}