package dao;

import db.DBConnection;
import model.Order;
import model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderDAO - handles the full order lifecycle.
 * 
 * Status flow:
 *   PLACED     → student submitted
 *   PREPARING  → cashier confirmed
 *   READY      → cook finished
 *   SERVED     → handed to student
 */
public class OrderDAO {

    // =====================================================
    // PLACE ORDER (student or cashier creates it)
    // =====================================================
    public int placeOrder(Order order) {
        String insertOrder = "INSERT INTO orders " +
                "(session_id, student_id, placed_by, total_amount, status) " +
                "VALUES (?, ?, ?, ?, 'PLACED')";

        String insertItem = "INSERT INTO order_items " +
                "(order_id, item_id, quantity, subtotal) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert order
            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(
                    insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getSessionId());
                ps.setInt(2, order.getStudentId());
                ps.setString(3, order.getPlacedBy());
                ps.setDouble(4, order.getTotalAmount());

                int rows = ps.executeUpdate();
                if (rows == 0) { conn.rollback(); return -1; }

                ResultSet keys = ps.getGeneratedKeys();
                if (!keys.next()) { conn.rollback(); return -1; }
                orderId = keys.getInt(1);
            }

            // 2. Insert order items
            try (PreparedStatement ps = conn.prepareStatement(insertItem)) {
                for (OrderItem item : order.getItems()) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.getItemId());
                    ps.setInt(3, item.getQuantity());
                    ps.setDouble(4, item.getSubtotal());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 3. Update session status to ORDERED
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE canteen_sessions SET status='ORDERED' " +
                    "WHERE session_id = ? AND status = 'IN_QUEUE'")) {
                ps.setInt(1, order.getSessionId());
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ Order placed! Order ID: " + orderId +
                    " | Total: ₹" + order.getTotalAmount());
            return orderId;

        } catch (SQLException e) {
            System.out.println("❌ placeOrder error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignore) {}
            }
            return -1;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignore) {}
            }
        }
    }

    // =====================================================
    // CASHIER: get all PLACED orders (waiting for confirmation)
    // =====================================================
    public List<Order> getPlacedOrders() {
        return fetchOrders("PLACED");
    }

    // =====================================================
    // COOK: get all PREPARING orders (FIFO — oldest first)
    // =====================================================
    public List<Order> getKitchenQueue() {
        return fetchOrders("PREPARING");
    }

    // =====================================================
    // CASHIER: get all READY orders (waiting for pickup)
    // =====================================================
    public List<Order> getReadyOrders() {
        return fetchOrders("READY");
    }

    // =====================================================
    // Helper: fetch orders by status + load their items
    // =====================================================
    private List<Order> fetchOrders(String status) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY order_time ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = mapRow(rs);
                o.setItems(getItemsForOrder(o.getOrderId()));
                list.add(o);
            }
        } catch (SQLException e) {
            System.out.println("❌ fetchOrders(" + status + ") error: " + e.getMessage());
        }
        return list;
    }

    /** Get all order items for a given order */
    public List<OrderItem> getItemsForOrder(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("subtotal")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ getItemsForOrder error: " + e.getMessage());
        }
        return items;
    }

    // =====================================================
    // CASHIER: Confirm order (PLACED → PREPARING)
    // =====================================================
    public boolean confirmOrder(int orderId, int staffId) {
        String sql = "UPDATE orders SET " +
                "status = 'PREPARING', " +
                "confirmed_by_staff_id = ?, " +
                "confirmed_at = NOW() " +
                "WHERE order_id = ? AND status = 'PLACED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setInt(2, orderId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Order #" + orderId + " confirmed → PREPARING");
                return true;
            }
            System.out.println("⚠️  Order #" + orderId + " not in PLACED state.");
            return false;
        } catch (SQLException e) {
            System.out.println("❌ confirmOrder error: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // COOK: Mark order READY (PREPARING → READY)
    // =====================================================
    public boolean markReady(int orderId) {
        String sql = "UPDATE orders SET status = 'READY', ready_at = NOW() " +
                "WHERE order_id = ? AND status = 'PREPARING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Order #" + orderId + " marked READY.");
                return true;
            }
            System.out.println("⚠️  Order #" + orderId + " not in PREPARING state.");
            return false;
        } catch (SQLException e) {
            System.out.println("❌ markReady error: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // CASHIER: Mark order SERVED (READY → SERVED)
    // =====================================================
    public boolean markServed(int orderId) {
        String sql = "UPDATE orders SET status = 'SERVED', served_at = NOW() " +
                "WHERE order_id = ? AND status = 'READY'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Order #" + orderId + " marked SERVED.");
                return true;
            }
            System.out.println("⚠️  Order #" + orderId + " not in READY state.");
            return false;
        } catch (SQLException e) {
            System.out.println("❌ markServed error: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // Get single order by ID
    // =====================================================
    public Order getById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Order o = mapRow(rs);
                o.setItems(getItemsForOrder(orderId));
                return o;
            }
        } catch (SQLException e) {
            System.out.println("❌ getById order error: " + e.getMessage());
        }
        return null;
    }

    // =====================================================
    // Get active order for a student (PLACED/PREPARING/READY)
    // =====================================================
    public Order getActiveOrderForStudent(int studentId) {
        String sql = "SELECT * FROM orders " +
                "WHERE student_id = ? AND status IN ('PLACED','PREPARING','READY') " +
                "ORDER BY order_time DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Order o = mapRow(rs);
                o.setItems(getItemsForOrder(o.getOrderId()));
                return o;
            }
        } catch (SQLException e) {
            System.out.println("❌ getActiveOrderForStudent error: " + e.getMessage());
        }
        return null;
    }

    // =====================================================
    // Helper: ResultSet → Order object
    // =====================================================
    private Order mapRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setSessionId(rs.getInt("session_id"));
        o.setStudentId(rs.getInt("student_id"));
        o.setPlacedBy(rs.getString("placed_by"));
        o.setConfirmedByStaffId((Integer) rs.getObject("confirmed_by_staff_id"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setOrderTime(rs.getTimestamp("order_time"));
        o.setConfirmedAt(rs.getTimestamp("confirmed_at"));
        o.setReadyAt(rs.getTimestamp("ready_at"));
        o.setServedAt(rs.getTimestamp("served_at"));
        o.setStatus(rs.getString("status"));
        return o;
    }
}