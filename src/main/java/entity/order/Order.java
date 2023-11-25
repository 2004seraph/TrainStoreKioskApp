package entity.order;

import db.DatabaseBridge;
import db.DatabaseOperation;
import db.DatabaseRecord;
import entity.product.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class Order extends DatabaseOperation.Entity implements DatabaseRecord {
    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String msg) {
            super(msg);
        }
    }
    private Integer orderId = -1;
    private Integer customerId;
    private Date date;

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        FULFILLED
    }
    private OrderStatus status;

    private List<OrderLine> items = new ArrayList<OrderLine>();

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public List<OrderLine> getItemsList() {
        return items;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return this.status;
    }
    public Date getDate() { return date; }
    public int getCustomerID() { return customerId; }

    public Order(Integer customerId) {
        this.customerId = customerId;
        this.date = new Date();
        this.status = OrderStatus.PENDING;
    }

    public Order(Integer customerId, Date date, OrderStatus status) {
        this.customerId = customerId;
        this.date = date;
        this.status = status;
    }

    private Order(Integer orderId, Integer customerId, Date date, OrderStatus status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.date = date;
        this.status = status;
    }

    private Order(Integer orderId, Integer customerId, Date date, OrderStatus status, List<OrderLine> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.date = date;
        this.status = status;
        this.items = items;
    }

    /**
     * Get the order with its items
     * @param orderId Primary key
     * @return Order entity with order lines
     * @throws SQLException
     */
    public static Order getOrderWithID(Integer orderId) throws SQLException {
        try (PreparedStatement findQuery = prepareStatement("SELECT * FROM Order WHERE orderId = ?")) {
            findQuery.setInt(1, orderId);
            ResultSet rs = findQuery.executeQuery();

            if (!rs.next()) {
                throw new Order.OrderNotFoundException("Failed to find order with orderId ["+orderId+"]");
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to find order with orderId ["+orderId+"]", e);
            throw e;
        }

        try (PreparedStatement orderQuery = prepareStatement("SELECT * FROM Order WHERE orderId = ?");
             PreparedStatement orderLineQuery = prepareStatement("SELECT * FROM OrderLine WHERE orderId = ?")
        ) {
            orderQuery.setInt(1, orderId);
            orderLineQuery.setInt(1, orderId);

            ResultSet rsOrder = orderQuery.executeQuery();
            ResultSet rsOrderLine = orderLineQuery.executeQuery();

            List<OrderLine> items = new ArrayList<OrderLine>();
            while (rsOrderLine.next()) {
                items.add(new OrderLine(
                        rsOrderLine.getInt("orderId"),
                        rsOrderLine.getString("productCode"),
                        rsOrderLine.getInt("quantity")
                ));
            }

            Order order = new Order(
                    rsOrder.getInt("orderId"),
                    rsOrder.getInt("personId"),
                    rsOrder.getDate("date"),
                    Order.OrderStatus.valueOf(rsOrder.getString(4)),
                    items
            );

            return order;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to fetch orders with orderId ["+orderId+"]", e);
            throw e;
        }
    }

    /**
     * Returns all orders with a given status
     * @param statuses A variadic list of either PENDING, CONFIRMED, FULFILLED
     * @return List of orders
     * @throws SQLException
     */
    public static List<Order> getOrdersWithStatus(Order.OrderStatus... statuses) throws SQLException {
        try {
            // build a query with all the status's the user wants to find
            StringBuilder statementText = new StringBuilder("SELECT * FROM `Order` WHERE");
            statementText.append(" status = ? OR".repeat(statuses.length));
            statementText.delete(statementText.lastIndexOf(" OR"), statementText.length());

            // populate said query
            PreparedStatement orderQuery = prepareStatement(statementText.toString());
            int i = 1;
            for (OrderStatus s : statuses) {
                orderQuery.setString(i, s.toString());
                i++;
            }

            ResultSet rs = orderQuery.executeQuery();

            List<Order> orders = new ArrayList<Order>();
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("orderId"),
                        rs.getInt("personId"),
                        rs.getDate("date"),
                        Order.OrderStatus.valueOf(rs.getString(4))
                ));
            }

            return orders;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to fetch orders with statuses: " + Arrays.toString(statuses), e);
            throw e;
        }
    }

    public static void createOrder(Order order) throws SQLException {
        setAutoCommit(false);
        int id;

        try (PreparedStatement s = prepareStatement("INSERT INTO Order VALUES (default,?,?,?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement r = prepareStatement("INSERT INTO OrderLine VALUES (?,?,?)");
        ) {

            Object[] fields = order.getFields().toArray();

            s.setInt(1, (Integer) fields[0]); // personId
            s.setString(2, (String) fields[1]); // date
            s.setString(3, fields[2].toString()); // status
            s.executeUpdate();

            ResultSet rs = s.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
                order.setOrderId(id);
            } else {
                throw new InternalError("Failed to insert into Order table");
            }

            int finalId = id;
            order.getItemsList().forEach((item) -> {
                Object[] olFields = item.getFields().toArray();

                try {
                    r.setInt(1, finalId);
                    r.setString(2, olFields[1].toString());
                    r.setInt(3, (Integer) olFields[2]);
                    r.executeUpdate();
                } catch (SQLException e) {
                    DatabaseBridge.databaseError("Failed to insert new order line", e);
                }

            });

            commit();
        } catch (SQLException | InternalError e) {
            DatabaseBridge.databaseError("Failed to insert new order", e);
            rollback();
            throw e;
        } finally {
            setAutoCommit(true);
        }
    }

    /**
     * Update the status of an order
     * @param orderId primary key of the order
     * @param newStatus should be either CONFIRMED, PENDING or FULFILLED
     * @return whether operation was successful
     * @throws SQLException
     */
    public static boolean updateOrderStatus(int orderId, Order.OrderStatus newStatus) throws SQLException {
        try (PreparedStatement findQuery = prepareStatement("SELECT * FROM Order WHERE orderId = ?")) {
            findQuery.setInt(1, orderId);
            ResultSet rs = findQuery.executeQuery();

            if (!rs.next()) {
                throw new Order.OrderNotFoundException("Failed to find order with orderId ["+orderId+"]");
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to find order with orderId ["+orderId+"]", e);
            throw e;
        }

        try (PreparedStatement query = prepareStatement("UPDATE Order SET status = ? WHERE orderId = ?")) {
            query.setString(1, newStatus.toString());
            query.setInt(2, orderId);

            int rows = query.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to update order with orderId ["+orderId+"] to status ["+newStatus+"]", e);
            throw e;
        }
    }

    public void addItem(Product product, Integer amount) {
        for (OrderLine line : items) {
            if (line.getProductCode().equals(product.getProductCode())) {
                line.setQuantity(line.getQuantity() + amount);
                return;
            }
        }

        OrderLine ol = new OrderLine(orderId, product.getProductCode(), amount);
        ol.setItem(product);

        items.add(ol);
    }

    public Double getTotalCost() {
        if (items.isEmpty()) {
            return 0.00;
        }

        Double total = 0.0;
        try {
            for (OrderLine line : items) {
                total += line.getItem().getPrice() * line.getQuantity();
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to get product whilst tallying total cost", e);
            throw new RuntimeException(e);
        }

        return total;
    }

    public List<Object> getFields() {
        List<Object> list = Arrays.asList(
                customerId,
                date.toString(),
                status.toString()
        );
        return list;
    }
}
