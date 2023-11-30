package gui.person;

import controllers.OrderController;
import entity.product.*;
import entity.product.Component;
import org.javatuples.Pair;
import utils.GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ShopCard extends JPanel {
    private final JButton addToCardBtn;
    GridBagConstraints gbc;
    GridBagLayout gbl;

    Product product;

    Integer quantity;
    JTextField quantityBox;

    public ShopCard(Product product) throws SQLException {
        this.product = product;

        gbc = new GridBagConstraints();
        gbl = new GridBagLayout();
//        gbl.setConstraints(this, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;

        gbl.setConstraints(this, gbc);
        setLayout(gbl);

        setBorder(BorderFactory.createLineBorder(Color.black));

        JLabel productName = new JLabel("<html><h2>"+product.getName()+"</h2></html>");
        add(productName, gbc);

        gbc.gridx = 1;
        JLabel price = new JLabel("<html><h4>"+GUI.ukCurrencyFormat.format(product.getPrice())+"</h4></html>");
        price.setHorizontalAlignment(SwingConstants.RIGHT);
        add(price, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;

        String productCode = product.getProductCode();
        switch (productCode.charAt(0)) {
            case 'L':
                productCode = "<html><h3>Locomotive</h3></html>";
                break;
            case 'R':
                productCode = "<html><h3>Track Piece</h3></html>";
                break;
            case 'C':
                productCode = "<html><h3>Controller</h3></html>";
                break;
            case 'S':
                productCode = "<html><h3>Rolling Stock</h3></html>";
                break;
            case 'M':
                productCode = "<html><h3>Train Set</h3></html>";
                break;
            case 'P':
                productCode = "<html><h3>Track Pack</h3></html>";
                break;
        }
        JLabel productCodeLabel = new JLabel(productCode);
        add(productCodeLabel, gbc);

        gbc.gridy = 2;

        try {
            if (product.isComponent()) {
                Component productComponent = product.getComponent();

                JLabel brand = new JLabel("Brand: "+productComponent.getBrand());
                add(brand, gbc);

                gbc.gridy++;

                JLabel gauge = new JLabel("Gauge: "+productComponent.getGauge().toString());
                add(gauge, gbc);

                gbc.gridy++;

                JLabel era = new JLabel("Era: "+productComponent.getEra().toString());
                add(era, gbc);

                if (productComponent.getClass().equals(Locomotive.class)) {
                    gbc.gridy++;
                    JLabel priceBracket = new JLabel("DCC Category: "+((Locomotive) productComponent).getPriceBracket().toString());
                    add(priceBracket, gbc);
                }

                if (productComponent.getClass().equals(Track.class)) {
                    gbc.gridy++;
                    JLabel curvature = new JLabel("Curvature: "+((Track) productComponent).getCurvature().toString());
                    add(curvature, gbc);
                }

                if (productComponent.getClass().equals(Controller.class)) {
                    gbc.gridy++;
                    JLabel controllerType = new JLabel("Controller Type: "+((Controller) productComponent).getControlType().toString());
                    add(controllerType, gbc);
                }

                JPanel quantityPanel = new JPanel();
                quantityPanel.setLayout(new BorderLayout());

                JLabel quantityLabel = new JLabel("Quantity: ");
                quantityLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                quantityBox = new JTextField();
                quantityBox.setPreferredSize(new Dimension(30, 24));
                quantityPanel.add(quantityLabel, BorderLayout.CENTER);
                quantityPanel.add(quantityBox, BorderLayout.EAST);

                gbc.gridy++;
                add(quantityPanel, gbc);
            } else {
                BoxedSet boxedSet = product.getBoxedSet();
                List<Pair<Component, Integer>> components = boxedSet.getComponents();
                List<Pair<BoxedSet, Integer>> subBoxedSets = boxedSet.getBoxedSets();

                JPanel componentPanel = new JPanel();
                JScrollPane scrollPane = new JScrollPane(componentPanel);
                scrollPane.setMaximumSize(new Dimension(0, 70));
                scrollPane.setMinimumSize(new Dimension(0, 70));
                scrollPane.setPreferredSize(new Dimension(0, 70));
                scrollPane.setVerticalScrollBar(new JScrollBar());

                componentPanel.setLayout(new GridLayout(0, 1));

                subBoxedSets.forEach((c) -> {
                    BoxedSet set = c.getValue0();
                    Integer amount = c.getValue1();
                    JLabel label = new JLabel(amount.toString()+"x "+set.getName());
                    componentPanel.add(label);
                });

                components.forEach((c) -> {
                    Product component = c.getValue0();
                    Integer amount = c.getValue1();
                    JLabel label = new JLabel(amount.toString()+"x "+component.getName());
                    componentPanel.add(label);
                });

                add(scrollPane, gbc);

                JPanel quantityPanel = new JPanel();
                quantityPanel.setLayout(new BorderLayout());

                JLabel quantityLabel = new JLabel("Quantity: ");
                quantityLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                quantityBox = new JTextField();
                quantityBox.setPreferredSize(new Dimension(30, 24));
                quantityPanel.add(quantityLabel, BorderLayout.CENTER);
                quantityPanel.add(quantityBox, BorderLayout.EAST);

                gbc.gridy++;
                add(quantityPanel, gbc);
            }
        } catch (SQLException e) {
            throw e;
        }

        addToCardBtn = new JButton("Add to Cart");
        if (product.getStockLevel() <= 0) {
            addToCardBtn.setEnabled(false);
            addToCardBtn.setToolTipText("Out of Stock");
        }

        gbc.gridx = 1;
        add(addToCardBtn, gbc);

        addToCardBtn.addActionListener((e) -> {
            if(quantityBox.getText().isEmpty()) {
                return;
            }

            quantity = Integer.valueOf(quantityBox.getText());
            Integer stockLevel = product.getStockLevel();
            if (quantity > stockLevel) {
                JOptionPane.showMessageDialog(this, "Sorry we have insufficient stock of this item. We currently only have "+stockLevel+" in stock.");
                return;
            }

            if (quantity < 1) {
                return;
            }
            OrderController.currentOrder.addItem(product, quantity);
            JOptionPane.showMessageDialog(this, "Added "+quantity+"x "+product.getName()+" to your cart.");
        });
    }
}
