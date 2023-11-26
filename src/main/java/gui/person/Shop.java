package gui.person;

import controllers.AppContext;
import db.DatabaseBridge;
import entity.product.Product;
import gui.components.TabbedGUIContainer;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Shop extends JPanel implements TabbedGUIContainer.TabPanel {
    private static final int cardSpacing = 30;

    private JPanel contentPanel;

    public Shop() {
        this.setLayout(new BorderLayout());
        contentPanel = new JPanel();
        GridLayout gl = new GridLayout(0, 2);
        gl.setHgap(cardSpacing);
        gl.setVgap(cardSpacing);
        contentPanel.setLayout(gl);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);

        // TO LOAD THE PRODUCTS
        loadStore();
    }

    private void loadStore() {
        contentPanel.removeAll();
        DatabaseBridge db = DatabaseBridge.instance();
        ArrayList<Product> productList = new ArrayList<>();

        try {
            db.openConnection();
            ResultSet products = Product.getAllProducts();
            assert products != null;

            while (products.next()) {
                productList.add(Product.getProductByID(products.getString(1)));
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Error whilst fetching all products", e);
            throw new RuntimeException(e);
        } finally {
            db.closeConnection();
        }

        // TO LOAD THE SATELLITE DATA AND ADD TO THE UI
        try {
            db.openConnection();
            for (Product p : productList) {
                contentPanel.add(new ShopCard(p));
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Error whilst fetching all products", e);
            throw new RuntimeException(e);
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {

    }

    @Override
    public void onSelected() {
        if (AppContext.queueStoreReload) {
            loadStore();
            AppContext.queueStoreReload = false;
        }
    }
}
