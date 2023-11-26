package gui.person;

import entity.order.Order;
import entity.order.OrderLine;
import entity.product.Product;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class PastOrders extends JPanel {
    GridBagConstraints gbc;
    GridBagLayout gbl;

    public PastOrders(Order order) {
        gbc = new GridBagConstraints();
        gbl = new GridBagLayout();
        gbc.fill = GridBagConstraints.BOTH;
        setLayout(gbl);

        setBorder(BorderFactory.createLineBorder(Color.black));

        JLabel orderIdLabel = new JLabel("Order ID: " + order.getOrderId());
        add(orderIdLabel, createGbc(0, 0));

        JLabel statusLabel = new JLabel("Status: " + order.getStatus().toString());
        add(statusLabel, createGbc(0, 1));

        JLabel dateLabel = new JLabel("Date: " + formatDate(order.getDate()));
        add(dateLabel, createGbc(0, 2));

        JLabel totalCostLabel = new JLabel("Total Cost: " + String.format("%.2f", order.getTotalCost()));
        add(totalCostLabel, createGbc(0, 3));

        JLabel itemsLabel = new JLabel("Items:");
        add(itemsLabel, createGbc(0, 4));

        for (OrderLine orderLine : order.getItemsList()) {
            try {
                Product product = orderLine.getItem();
                JLabel itemLabel = new JLabel(orderLine.getQuantity() + "x " + product.getName() +
                        " (Unit Price: " + String.format("%.2f", product.getPrice()) + ")");
                add(itemLabel, createGbc(0, GridBagConstraints.RELATIVE));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String formatDate(java.util.Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }
}
