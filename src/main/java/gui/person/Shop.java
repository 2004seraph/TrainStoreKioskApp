package gui.person;

import db.DatabaseBridge;
import entity.product.Product;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Shop extends JPanel {
    public Shop() {
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        GridLayout gl = new GridLayout(0, 3);
        gl.setHgap(40);
        gl.setVgap(40);
        contentPanel.setLayout(gl);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);

        DatabaseBridge db = DatabaseBridge.instance();
        int maxLoad = 3;
        ArrayList<Product> productList = new ArrayList<>();
        try {
            db.openConnection();
            ResultSet products = Product.getAllProducts();
            assert products != null;

            while (products.next()) {
//                db.openConnection(); // Le epic
                productList.add(Product.getProductByID(products.getString(1)));
                //contentPanel.add(new ShopCard(product));
//                db.closeConnection();
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Error whilst fetching all products", e);
            throw new RuntimeException(e);
        } finally {
            db.closeConnection();
        }

        for (Product p : productList) {
            contentPanel.add(new ShopCard(p));
        }
    }
}
