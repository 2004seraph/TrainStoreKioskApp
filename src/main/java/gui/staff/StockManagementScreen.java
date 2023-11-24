package gui.staff;

import controllers.AppContext;
import db.DatabaseBridge;
import db.DatabaseOperation;
import entity.product.Product;
import gui.components.ButtonColumn;
import gui.components.CurrencyCellRenderer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockManagementScreen extends JPanel {
    private final String[] columns = new String[]{"Code", "Product", "Price Per Unit", "Stock", "Delete Item"};

    public StockManagementScreen() {
        Object[][] productData;

        try {
            DatabaseBridge.instance().openConnection();
            PreparedStatement countQuery = DatabaseBridge.instance().prepareStatement("SELECT COUNT(*) FROM Product;");
            ResultSet res = countQuery.executeQuery();
            int count = -1;
            if (res.next()) {
                count = res.getInt(1);
            } else {
                throw new RuntimeException("Could not get product data");
            }

            ResultSet products = Product.getAllProducts();
            productData = new Object[count - 1][columns.length];
            int index = 0;
            while (true) {
                assert products != null;
                if (!products.next()) break;
                productData[index][0] = products.getString("productCode");
                productData[index][1] = products.getString("name");
                productData[index][2] = products.getDouble("price");
                productData[index][3] = products.getInt("stockLevel");
                ++index;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseBridge.instance().closeConnection();
        }

        setLayout(new GridLayout());

        JTable jt = new JTable(new StockManagementModel(productData, columns));
        jt.getColumnModel().getColumn(2).setCellRenderer(new CurrencyCellRenderer());
        ButtonColumn.setButtonColumn(jt.getColumn("Delete Item"), new ButtonColumn.TextFunction() {
            @Override
            public String setText(int row, int column) {
                return "Delete";
            }
        }, new ButtonColumn.ActionFunction() {
            @Override
            public void onClick(int row, int column) {
                System.out.println("pushed editor: " + row);
            }
        });

        JScrollPane scrollPane = new JScrollPane(jt);
        this.add(scrollPane);

        // refresh database view button
    }

    public static void main(String[] args) {
        DatabaseOperation.setConnection(DatabaseBridge.instance());
        JFrame win = AppContext.getWindow();
        win.add(new StockManagementScreen());
        win.setVisible(true);
    }

    private static class StockManagementModel extends AbstractTableModel {

        private Object[][] productData;
        private String[] columns;

        public StockManagementModel(Object[][] productData, String[] columns) {
            this.productData = productData;
            this.columns = columns;
        }

        @Override
        public int getRowCount() {
            return this.productData.length;
        }

        @Override
        public int getColumnCount() {
            return this.columns.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return this.productData[rowIndex][columnIndex];
        }

        @Override
        public String getColumnName(int columnIndex) {
            return this.columns[columnIndex];
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            DatabaseBridge db = DatabaseBridge.instance();
            PreparedStatement update;
            try {
                db.openConnection();
                switch (columnIndex) {
                    case 1: // name
                        update = db.prepareStatement("UPDATE Product SET name=? WHERE productCode=?");
                        update.setString(1, (String)value);
                        break;
                    case 2: // price
                        update = db.prepareStatement("UPDATE Product SET price=? WHERE productCode=?");
                        update.setDouble(1, (double)value);
                        break;
                    case 3: // stock
                        update = db.prepareStatement("UPDATE Product SET stockLevel=? WHERE productCode=?");
                        update.setInt(1, (int)value);
                        break;
                    default:
                        return;
                }

                update.setString(2, (String)productData[rowIndex][0]);
                update.executeUpdate();
                this.productData[rowIndex][columnIndex] = value;
            } catch (SQLException e) {
                DatabaseBridge.databaseError("Could not edit stock", e);
            } finally {
                db.closeConnection();
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return ! (columnIndex == 0);// || columnIndex == 4);
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: return String.class;
                case 1: return String.class;
                case 2: return Double.class;
                case 3: return Integer.class;
                default: return String.class;
            }
        }
    }
}

