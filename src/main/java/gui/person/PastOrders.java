package gui.person;

import entity.order.Order;
import entity.order.OrderLine;
import entity.product.Product;

import javax.swing.*;
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

        JLabel orderIdLabel = new JLabel("Order ID: " + order.getOrderId());
        gbc.gridy = 0;
        add(orderIdLabel, gbc);

        JLabel dateLabel = new JLabel("Date: " + formatDate(order.getDate()));
        gbc.gridy++;
        add(dateLabel, gbc);

        JLabel statusLabel = new JLabel("Status: " + order.getStatus().toString());
        gbc.gridy++;
        add(statusLabel, gbc);

        JLabel totalCostLabel = new JLabel("Total Cost: " + String.format("%.2f", order.getTotalCost()));
        gbc.gridy++;
        add(totalCostLabel, gbc);

        JLabel itemsLabel = new JLabel("Items:");
        gbc.gridy++;
        add(itemsLabel, gbc);

        for (OrderLine orderLine : order.getItemsList()) {
            try {
                JPanel orderLinePanel = new JPanel(new GridLayout(1, 3));

                Product product = orderLine.getItem();

                JLabel itemNameLabel = new JLabel("Product: " + product.getName());
                orderLinePanel.add(itemNameLabel);

                JLabel quantityLabel = new JLabel("Quantity: " + orderLine.getQuantity());
                orderLinePanel.add(quantityLabel);

                JLabel unitPriceLabel = new JLabel("Unit Price: " + String.format("%.2f", product.getPrice()));
                orderLinePanel.add(unitPriceLabel);

                gbc.gridy++;
                add(orderLinePanel, gbc);
            } catch (SQLException e) {
                // Handle the exception appropriately in your application or log it
                e.printStackTrace();
            }
        }
    }

    private String formatDate(java.util.Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}
