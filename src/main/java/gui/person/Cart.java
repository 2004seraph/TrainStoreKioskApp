package gui.person;

import controllers.OrderController;
import db.DatabaseBridge;
import entity.order.Order;
import entity.order.OrderLine;
import entity.product.Product;
import gui.components.TabbedGUIContainer;
import utils.GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class Cart extends JPanel implements TabbedGUIContainer.TabPanel {
    private final JPanel contentPanel;
    private final GridBagConstraints gbc;

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {

    }

    @Override
    public void onSelected() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshCart();
            }
        });
    }

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
            JLabel total = new JLabel("Subtotal: "+GUI.ukCurrencyFormat.format(quantity * product.getPrice()));
            add(total, gbc);
        }
    }

    public Cart() {
        contentPanel = new JPanel();
        JPanel headerPanel = new JPanel();
        setLayout(new BorderLayout());

        add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);

        gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbl.setConstraints(headerPanel, gbc);

        headerPanel.setLayout(gbl);
        contentPanel.setLayout(gbl);

        JLabel title = new JLabel("<html><h1>Cart</h1></html>");
        title.setBorder(new EmptyBorder(0, 6, 0, 0));
        headerPanel.add(title, gbc);

        gbc.gridy++;
        JLabel infoLabel = new JLabel("Review your order and head to checkout");
        int infoInset = 7;
        infoLabel.setBorder(new EmptyBorder(infoInset, infoInset, infoInset, infoInset));
        headerPanel.add(infoLabel, gbc);

        gbc.gridy++;
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setBorder(new EmptyBorder(0, 0, 10, 0));
        headerPanel.add(sep, gbc);
        JButton refreshCartBtn = new JButton("Refresh Cart");
        gbc.gridy++;
        gbc.gridwidth = 1;
        headerPanel.add(refreshCartBtn, gbc);

        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

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
        JLabel totalCost = new JLabel("<html><b>Total: </b>"
                + GUI.ukCurrencyFormat.format(OrderController.currentOrder.getTotalCost()) + "</html>");
        JButton checkoutBtn = new JButton("Checkout");
        checkoutPanel.add(totalCost);
        checkoutPanel.add(checkoutBtn);

        checkoutBtn.addActionListener((e) -> {
            boolean success = OrderController.checkout();
            if (!success) {
                JOptionPane.showMessageDialog(this, "Your bank account details are missing or invalid", "Something went wrong", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Order successfully placed");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        refreshCart();
                    }
                });
            }
        });

        if (contentPanel.getComponents().length > 0) {
            contentPanel.add(checkoutPanel, gbc);
        }

        revalidate();
        repaint();
    }
}
