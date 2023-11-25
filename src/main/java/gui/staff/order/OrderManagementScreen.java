package gui.staff.order;

import db.DatabaseBridge;
import entity.order.Order;
import entity.product.Product;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class OrderManagementScreen extends JPanel {
    private final String[] columns = new String[]{"Order ID", "Date", "Customer", "Address", "Payment", "Status"};

    private Object[][] orderData;

    public OrderManagementScreen() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        this.setLayout(gbl);

        // jtable with all orders, status screen below which displays the full order when the row is selected
        // basic filters at the top

        orderData = loadData();
        System.out.println(Arrays.toString(orderData));

        JTable jt = new JTable(orderData, columns);
        JScrollPane scrollPane = new JScrollPane(jt);
        add(scrollPane, gbc);
    }

    private Object[][] loadData() {
        Object[][] orderData;
        List<Order> orders;

        // will need to query a customer payment and address for each order

        try {
            DatabaseBridge.instance().openConnection();
            orders = Order.getOrdersWithStatus(Order.OrderStatus.CONFIRMED, Order.OrderStatus.FULFILLED);

            if (orders.isEmpty()) {
                return new Object[0][columns.length];
            }
            orderData = new Object[orders.size()][columns.length];

            for (int i = 0; i < orders.size(); i++) {
                Order o = orders.get(i);
                orderData[i][0] = o.getOrderId();
                orderData[i][1] = o.getDate().toString();
                orderData[i][2] = o.getCustomerID();
                // get address
                // get payment
                orderData[i][5] = o.getStatus();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseBridge.instance().closeConnection();
        }

        return orderData;
    }
}
