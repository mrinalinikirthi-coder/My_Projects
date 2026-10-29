package model;

/**
 * OrderItem - one line in an order (item + quantity)
 */
public class OrderItem {
    private int id;
    private int orderId;
    private int itemId;
    private int quantity;
    private double subtotal;

    // For creating new order items (before DB insert)
    public OrderItem(int itemId, int quantity, double subtotal) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public OrderItem(int id, int orderId, int itemId, int quantity, double subtotal) {
        this.id = id;
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public int getId()            { return id; }
    public int getOrderId()       { return orderId; }
    public int getItemId()        { return itemId; }
    public int getQuantity()      { return quantity; }
    public double getSubtotal()   { return subtotal; }

    public void setId(int id)                   { this.id = id; }
    public void setOrderId(int orderId)         { this.orderId = orderId; }
    public void setItemId(int itemId)           { this.itemId = itemId; }
    public void setQuantity(int quantity)       { this.quantity = quantity; }
    public void setSubtotal(double subtotal)    { this.subtotal = subtotal; }
}