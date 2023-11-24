package gui.staff.stock;

import controllers.AppContext;
import db.DatabaseBridge;
import db.DatabaseOperation;
import entity.product.Product;
import gui.components.ButtonColumn;
import gui.components.CurrencyCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockManagementScreen extends JPanel {
    private final String[] columns = new String[]{"Code", "Product", "Price Per Unit", "Stock", "Delete Item"};

    JPanel viewContainer;

    public StockManagementScreen() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        this.setLayout(gbl);

        JButton refreshButton = new JButton("Refresh Product List");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStockView();
            }
        });
        JButton newProductButton = new JButton("Add new product");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("New product");
                updateStockView();
            }
        });

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(refreshButton, gbc);

        gbc.gridx = 1;
        add(newProductButton, gbc);

        gbc.weightx = 1;
        gbc.weighty = 0.2;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        viewContainer = new JPanel();
        viewContainer.setLayout(new GridLayout());
        updateStockView();
        this.add(viewContainer, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        this.add(new CreateProductPanel(), gbc);
    }

    private void updateStockView() {
        viewContainer.removeAll();
        createStockView(viewContainer);

        revalidate();
        repaint();
    }

    private void createStockView(JPanel container) {
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

        JTable jt = new JTable(new StockManagementTableModel(productData, columns));
        jt.setRowHeight(24);
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
        container.add(scrollPane);
    }

    public static void main(String[] args) {
        DatabaseOperation.setConnection(DatabaseBridge.instance());
        JFrame win = AppContext.getWindow();
        win.add(new StockManagementScreen());
        win.setVisible(true);
    }
}

