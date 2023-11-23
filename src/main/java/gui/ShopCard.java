package gui;

import entity.product.Product;

import javax.swing.*;
import java.awt.*;

public class ShopCard extends JPanel {
    GridBagConstraints gbc;
    GridBagLayout gbl;

    Product product;
    public ShopCard(Product product) {
        this.product = product;

        gbc = new GridBagConstraints();
        gbl = new GridBagLayout();
        gbl.setConstraints(null, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel productName = new JLabel("<html><h1>"+product.getName()+"</h1></html>");
        add(productName);

        gbc.gridx = 1;
        JLabel price = new JLabel("£" + product.getPrice());
        add(productName);

        gbc.gridx = 0;
        gbc.gridy = 1;

        JLabel productCode = new JLabel(product.getProductCode());
        add(productCode);

        gbc.gridy = 2;


    }
}
