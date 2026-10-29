package model;

/**
 * Staff - represents a row in the 'staff' table
 * Roles: CASHIER (bills orders) or COOK (prepares food)
 */
public class Staff {
    private int staffId;
    private String name;
    private String role;      // CASHIER / COOK / MANAGER
    private String loginPin;

    public Staff() {}

    public Staff(int staffId, String name, String role, String loginPin) {
        this.staffId = staffId;
        this.name = name;
        this.role = role;
        this.loginPin = loginPin;
    }

    // Getters
    public int getStaffId()      { return staffId; }
    public String getName()      { return name; }
    public String getRole()      { return role; }
    public String getLoginPin()  { return loginPin; }

    // Setters
    public void setStaffId(int staffId)          { this.staffId = staffId; }
    public void setName(String name)             { this.name = name; }
    public void setRole(String role)             { this.role = role; }
    public void setLoginPin(String loginPin)     { this.loginPin = loginPin; }

    // Helper methods
    public boolean isCashier() { return "CASHIER".equals(role); }
    public boolean isCook()    { return "COOK".equals(role); }

    @Override
    public String toString() {
        return String.format("Staff #%d | %s | %s", staffId, name, role);
    }
}