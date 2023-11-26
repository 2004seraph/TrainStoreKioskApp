package gui.person;

import entity.order.Order;

import javax.swing.*;

import controllers.AppContext;
import db.DatabaseBridge;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PastOrdersScreen extends JPanel {
    private static final int orderSpacing = 30;

    public PastOrdersScreen() {
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        GridLayout gl = new GridLayout(0, 2);
        gl.setHgap(orderSpacing);
        gl.setVgap(orderSpacing);
        contentPanel.setLayout(gl);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);

        DatabaseBridge db = DatabaseBridge.instance();

        try {
            db.openConnection();
            List<Order> orders = AppContext.getCurrentUser().getAllOrders();
            for (Order o : orders) {
                contentPanel.add(new PastOrders(o));
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Error whilst fetching all products", e);
            throw new RuntimeException(e);
        } finally {
            db.closeConnection();
        }

    }
}
