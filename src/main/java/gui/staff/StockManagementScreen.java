package gui.staff;

import controllers.AppContext;
import db.DatabaseBridge;
import db.DatabaseOperation;
import entity.product.Product;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StockManagementScreen extends JPanel {
    private static class StockLine extends JPanel {
        private Product product = null;

        public StockLine(String productID) {
            DatabaseBridge db = DatabaseBridge.instance();
            try {
                db.openConnection();
                this.product = Product.getProductByID(productID);
            } catch (SQLException e) {
                DatabaseBridge.databaseError("Could not find product ID", e);
                throw new RuntimeException();
            } finally {
                db.closeConnection();
            }

            GridLayout gridLayout = new GridLayout(0,3);
            JPanel content = new JPanel();
            content.setLayout(gridLayout);
            add(content);

            content.add(new JLabel(product.getName()));
            content.add(new JSeparator(SwingConstants.VERTICAL));
            content.add(new JLabel(product.getStockLevel().toString()));
        }
    }

    public StockManagementScreen() {
        add(new StockLine("S1234"));
    }

    public static void main(String[] args) {
        DatabaseOperation.setConnection(DatabaseBridge.instance());
        JFrame win = AppContext.getWindow();
        win.add(new StockManagementScreen());
        win.setVisible(true);
    }
}
