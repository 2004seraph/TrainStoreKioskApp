package gui.person;

import controllers.OrderController;
import db.DatabaseBridge;
import entity.order.Order;
import entity.order.OrderLine;
import entity.product.Product;
import utils.GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Cart extends JPanel {
    private final JPanel contentPanel;
    private final GridBagConstraints gbc;

    public class OrderItem extends JPanel {
        OrderLine ol;
        Product product;

        JTextField quantityBox;
        Integer quantity;
        public OrderItem(OrderLine ol) {
            this.ol = ol;
            try {
                this.product = ol.getItem();
            } catch (SQLException e) {
                DatabaseBridge.databaseError("Failed to get product from orderline code ["+ol.getProductCode()+"]", e);
                throw new RuntimeException(e);
            }

            this.quantity = ol.getQuantity();

            setBorder(BorderFactory.createLineBorder(Color.black));

            GridBagLayout gbl = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            setLayout(gbl);

            gbl.setConstraints(this, gbc);

            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 0;
            gbc.gridy = 0;

            JLabel productName = new JLabel("<html><h2>"+product.getName() + " ("+ol.getProductCode()+")</h2></html>");
            add(productName, gbc);

            gbc.gridy = 1;
            JLabel quantityLabel = new JLabel("Quantity: ");
            add(quantityLabel, gbc);

            //TODO: Make functional
            gbc.gridx = 1;
            quantityBox = new JTextField();
            quantityBox.setPreferredSize(new Dimension(30, 24));
            quantityBox.setText(quantity.toString());
            add(quantityBox, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            JLabel unitPrice = new JLabel("Unit Price: "+ GUI.ukCurrencyFormat.format(product.getPrice()));
            add(unitPrice, gbc);

            gbc.gridx = 1;
            JLabel total = new JLabel("Total: "+GUI.ukCurrencyFormat.format(quantity * product.getPrice()));
            add(total, gbc);
        }
    }

    public Cart() {
        contentPanel = new JPanel();
        setLayout(new BorderLayout());

        gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbl.setConstraints(contentPanel, gbc);
        contentPanel.setLayout(gbl);

        add(contentPanel, BorderLayout.CENTER);

        JButton refreshCartBtn = new JButton("Refresh Cart");
        add(refreshCartBtn, BorderLayout.NORTH);

        refreshCartBtn.addActionListener((e) -> {
            refreshCart();
        });
    }

    public void refreshCart() {
        contentPanel.removeAll();
        gbc.gridx = 0;
        gbc.gridy = 0;

        OrderController.currentOrder.getItemsList().forEach((ol) -> {
            gbc.gridy++;
            contentPanel.add(new OrderItem(ol), gbc);
        });

        gbc.gridy++;

        JPanel checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new GridLayout(1, 2));
        JLabel totalCost = new JLabel("Total: "
                + GUI.ukCurrencyFormat.format(OrderController.currentOrder.getTotalCost()));
        JButton checkoutBtn = new JButton("Checkout");
        checkoutPanel.add(totalCost);
        checkoutPanel.add(checkoutBtn);

        contentPanel.add(checkoutPanel, gbc);

        revalidate();
        repaint();
    }
}
