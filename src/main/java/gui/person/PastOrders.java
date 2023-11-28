package gui.person;

import javax.swing.*;

import entity.order.Order;
import entity.order.OrderLine;
import entity.product.Product;

import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class PastOrders extends JPanel {
    private GridBagConstraints gbc;
    private GridBagLayout gbl;

    public PastOrders(Order order) {
        gbc = new GridBagConstraints();
        gbl = new GridBagLayout();
        gbc.fill = GridBagConstraints.BOTH;
        setLayout(gbl);
        setBorder(BorderFactory.createLineBorder(Color.black));

        addLabel("Order ID: " + order.getOrderId(), 0);
        addLabel("Date: " + formatDate(order.getDate()), 1);
        addLabel("Status: " + order.getStatus().toString(), 2);
        addLabel("Total Cost: " + String.format("%.2f", order.getTotalCost()), 3);

        JPanel itemsPanel = new JPanel(new GridLayout(order.getItemsList().size() + 1, 3));
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Items"));

        itemsPanel.add(new JLabel("Product"));
        itemsPanel.add(new JLabel("Quantity"));
        itemsPanel.add(new JLabel("Unit Price"));

        // Iterating through order items and adding them to the panel
        for (OrderLine orderLine : order.getItemsList()) {
            try {
                Product product = orderLine.getItem();
                itemsPanel.add(new JLabel(product.getName()));
                itemsPanel.add(new JLabel(String.valueOf(orderLine.getQuantity())));
                itemsPanel.add(new JLabel(String.format("%.2f", product.getPrice())));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(itemsPanel, gbc);
    }

    // Helper method to add a label with specified text at a given y-position
    private void addLabel(String text, int yPos) {
        JLabel label = new JLabel(text);
        gbc.gridy = yPos;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(label, gbc);
    }

    // Helper method to format a date using a SimpleDateFormat
    private String formatDate(java.util.Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}
