package gui.person;

import db.DatabaseBridge;
import entity.product.Product;
import gui.ShopCard;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Shop extends JPanel {
    public Shop() {
        setLayout(new GridLayout(0, 3));

        DatabaseBridge db = DatabaseBridge.instance();
        try {
            db.openConnection();
            Product thing = Product.getProductByID("L3478");
            ShopCard card = new ShopCard(thing);
            add(card);
        } catch (SQLException e) {
            System.out.println("cry about it");
        } finally {
            db.closeConnection();
        }
    }
}
