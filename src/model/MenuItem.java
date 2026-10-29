package model;

/**
 * MenuItem - represents a row in the 'menu_items' table
 */
public class MenuItem {
    private int itemId;
    private String itemName;
    private double price;
    private String category;
    private int avgPrepTimeMin;
    private boolean isAvailable;

    public MenuItem() {}

    public MenuItem(int itemId, String itemName, double price,
                    String category, int avgPrepTimeMin, boolean isAvailable) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.category = category;
        this.avgPrepTimeMin = avgPrepTimeMin;
        this.isAvailable = isAvailable;
    }

    // Getters
    public int getItemId()              { return itemId; }
    public String getItemName()         { return itemName; }
    public double getPrice()            { return price; }
    public String getCategory()         { return category; }
    public int getAvgPrepTimeMin()      { return avgPrepTimeMin; }
    public boolean isAvailable()        { return isAvailable; }

    // Setters
    public void setItemId(int itemId)                       { this.itemId = itemId; }
    public void setItemName(String itemName)                { this.itemName = itemName; }
    public void setPrice(double price)                      { this.price = price; }
    public void setCategory(String category)                { this.category = category; }
    public void setAvgPrepTimeMin(int avgPrepTimeMin)       { this.avgPrepTimeMin = avgPrepTimeMin; }
    public void setAvailable(boolean available)             { this.isAvailable = available; }

    @Override
    public String toString() {
        return String.format("#%d  %-15s  ₹%.2f  [%s]", itemId, itemName, price, category);
    }
}