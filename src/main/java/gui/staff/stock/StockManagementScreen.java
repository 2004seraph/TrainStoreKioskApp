package gui.staff.stock;

import controllers.AppContext;
import db.DatabaseBridge;
import db.DatabaseOperation;
import entity.product.Product;
import gui.components.ButtonColumn;
import gui.components.CurrencyCellRenderer;
import gui.components.TabbedGUIContainer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockManagementScreen extends JPanel implements TabbedGUIContainer.TabPanel {
    private final String[] columns = new String[]{"Code", "Product", "Price Per Unit", "Stock", "Delete Item"};

    JPanel viewContainer;

    JPanel productCreationContainer;

    Object[][] productData;

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

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.gridwidth = 2;
        JLabel title = new JLabel("<html><h1>Stock Management</h1></html>");
        title.setBorder(new EmptyBorder(0, 6, 0, 0));
        add(title, gbc);
        gbc.gridy++;

        JLabel infoLabel = new JLabel("Double click any cell to update its content and have it immediately written to the stock record.");
        int infoInset = 7;
        infoLabel.setBorder(new EmptyBorder(infoInset, infoInset, infoInset, infoInset));
        add(infoLabel, gbc);
        gbc.gridy++;

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(sep, gbc);
        gbc.gridwidth = 1;

        gbc.gridy++;
        add(refreshButton, gbc);

//        gbc.gridx = 1;
//        add(newProductButton, gbc);

        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        viewContainer = new JPanel();
        viewContainer.setLayout(new GridLayout());
        createStockView(viewContainer);
        this.add(viewContainer, gbc);

        gbc.gridy++;
        gbc.weighty = 0;

        productCreationContainer = new JPanel();
        GridBagLayout gbl2 = new GridBagLayout();
        productCreationContainer.setLayout(gbl2);
        createProductForm();
        this.add(productCreationContainer, gbc);
    }

    private void createProductForm() {
        productCreationContainer.removeAll();
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weighty = 1;
        gbc2.weightx = 1;
        CreateProductPanel cpp = new CreateProductPanel(productData, new Runnable() {
            @Override
            public void run() {
                updateStockView();
            }
        });
        productCreationContainer.add(cpp, gbc2);
    }

    private void updateStockView() {
        viewContainer.removeAll();
        createStockView(viewContainer);
        createProductForm();

        revalidate();
        repaint();
    }

    private void createStockView(JPanel container) {
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
            assert products != null;
            productData = new Object[count][columns.length];
            int index = 0;
            while (true) {
                productData[index][0] = products.getString("productCode");
                productData[index][1] = products.getString("name");
                productData[index][2] = products.getDouble("price");
                productData[index][3] = products.getInt("stockLevel");
                if (!products.next()) break;
                index++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseBridge.instance().closeConnection();
        }

        JTable jt = new JTable(new StockManagementTableModel(productData, columns, new Runnable() {
            @Override
            public void run() {
                createProductForm();
            }
        }));
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
                DatabaseBridge db = DatabaseBridge.instance();
                try {
                    db.openConnection();
                    // THIS CODE WORKS BUT I DO NOT WANT STUFF TO BE DELETED RIGHT NOW, WE NEED TO TEST THE APP
                    PreparedStatement productDeletion = DatabaseBridge.instance().prepareStatement("DELETE FROM Product WHERE productCode=?;");
                    productDeletion.setString(1, (String)jt.getValueAt(row, 0));
                    productDeletion.executeUpdate();
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Deleted '" + (String)jt.getValueAt(row, 1) + "' from products.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            StockManagementScreen.this.updateStockView();
                        }
                    });
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Could not delete item, it is referenced in past or present orders.", "Error", JOptionPane.WARNING_MESSAGE);
                    throw new RuntimeException(e);
                } finally {
                    db.closeConnection();
                }
                AppContext.queueStoreReload = true;
            }
        });

        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jt.getModel());
        jt.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>(3);
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        JScrollPane scrollPane = new JScrollPane(jt);
        container.add(scrollPane);
    }

    public static void main(String[] args) {
        DatabaseOperation.setConnection(DatabaseBridge.instance());
        JFrame win = AppContext.getWindow();
        win.add(new StockManagementScreen());
        win.setVisible(true);
    }

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {

    }

    @Override
    public void onSelected() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateStockView();
            }
        });
    }
}

