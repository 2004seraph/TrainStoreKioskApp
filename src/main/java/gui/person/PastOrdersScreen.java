package gui.person;

import entity.order.Order;

import javax.swing.*;

import controllers.AppContext;
import db.DatabaseBridge;
import gui.components.TabbedGUIContainer;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PastOrdersScreen extends JPanel implements TabbedGUIContainer.TabPanel{
    private static final int orderSpacing = 30;

    JPanel contentPanel;

    @Override
    public void onSelected() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshCart();
            }
        });
    }

    public PastOrdersScreen() {
        this.setLayout(new BorderLayout());
        contentPanel = new JPanel();
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
            DatabaseBridge.databaseError("Error whilst fetching all orders", e);
            throw new RuntimeException(e);
        } finally {
            db.closeConnection();
        }

    }

//    Implement a refreshCart() method that will refresh the cart everytime the page is selected
    private void refreshCart() {
        contentPanel.removeAll();
        DatabaseBridge db = DatabaseBridge.instance();

        try {
            db.openConnection();
            List<Order> orders = AppContext.getCurrentUser().getAllOrders();
            for (Order o : orders) {
                contentPanel.add(new PastOrders(o));
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Error whilst fetching all orders", e);
            throw new RuntimeException(e);
        } finally {
            db.closeConnection();
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {
        
    }

}
