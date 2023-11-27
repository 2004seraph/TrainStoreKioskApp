package gui.staff.order;

import controllers.AppContext;
import db.DatabaseBridge;
import entity.order.Order;
import entity.order.OrderLine;
import entity.product.Product;
import entity.user.Person;
import gui.components.CurrencyCellRenderer;
import gui.components.TabbedGUIContainer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static utils.GUI.setEnabledRecursively;

public class OrderManagementScreen extends JPanel implements TabbedGUIContainer.TabPanel {
    private final String[] orderViewColumns = new String[]{"Order ID", "Date", "Customer", "Email", "Address", "Payment", "Status"};
    private final String[] orderLineColumns = new String[]{"Product", "Brand", "Name", "Quantity", "SubTotal"};
    // the related product code, the brand and product name, the quantity, and the derived line-cost.

    private List<Order> orders;
    private Object[][] orderData;

    private final JPanel orderViewContainer;
    private JTable orderList;
    private final JPanel orderControls;
    private JTable orderContents;
    private JLabel orderTotal;

    private int lastSelectedRow = -1;
    private Order lastSelectedOrder = null;

    public OrderManagementScreen() {
        // jtable with all orders, status screen below which displays the full order when the row is selected
        // basic filters at the top

        orderData = loadData();

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        this.setLayout(gbl);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel title = new JLabel("<html><h1>Order Management</h1></html>");
        title.setBorder(new EmptyBorder(0, 6, 0, 0));
        add(title, gbc);
        gbc.gridy++;

        JLabel infoLabel = new JLabel("Click any order to view its contents and perform operations.");
        int infoInset = 7;
        infoLabel.setBorder(new EmptyBorder(infoInset, infoInset, infoInset, infoInset));
        add(infoLabel, gbc);
        gbc.gridy++;

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(sep, gbc);
        gbc.gridy++;

        JButton refreshButton = new JButton("Refresh View");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                    }
                });
            }
        });
        add(refreshButton, gbc);

        gbc.gridy++;
        gbc.weighty = 1;
        orderViewContainer = new JPanel();
        orderViewContainer.setLayout(new GridBagLayout());
        add(orderViewContainer, gbc);
        refreshData();

        gbc.gridy++;
        orderControls = createOrderControls();
        add(orderControls, gbc);

        resetState();
    }

    private JPanel createOrderControls() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("<html><h2>Order Details</h2></html>");
        title.setBorder(new EmptyBorder(0,6,0,0));
        panel.add(title, gbc);
        gbc.gridwidth = 1;

        gbc.gridy++;
        {
            gbc.weighty = 1;
            gbc.gridheight = 3;
            JPanel controls = createOrderActions();
//            controls.setBackground(Color.BLUE);
            panel.add(controls, gbc);
            gbc.gridheight = 1;
        }
        {
            gbc.gridx++;
            gbc.weighty = 0;
            JLabel subtitle = new JLabel("<html><h3>Order Contents</h3></html>");
            subtitle.setBorder(new EmptyBorder(0,6,0,0));
            panel.add(subtitle, gbc);
            gbc.gridy++;
            gbc.weighty = 1;
            orderContents = new JTable(new DefaultTableModel());
            orderContents.setRowHeight(24);
            JScrollPane scrollPane = new JScrollPane(orderContents);
            panel.add(scrollPane, gbc);

            orderTotal = new JLabel("<html><b>Total:</b> -</html>", SwingConstants.RIGHT);
            orderTotal.setBorder(new EmptyBorder(10, 10, 10, 10));
            gbc.gridy++;
            gbc.weighty = 0;
            panel.add(orderTotal, gbc);
        }

        return panel;
    }

    private JPanel createOrderActions() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.1;

        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton fulfillOrder = new JButton("Fulfill Order (deducts stock from the database)");
        panel.add(fulfillOrder, gbc);
        fulfillOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if payment valid, if stock valid

                if (!Objects.equals((String) orderList.getValueAt(lastSelectedRow, 5), "VALID")) {
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Could not fulfill order: No payment specified", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // for each orderline, deduct it's stock, break if negative
                // mark order as fulfilled

                DatabaseBridge db = DatabaseBridge.instance();
                try {
                    db.openConnection();
                    db.setAutoCommit(false);

                    for (OrderLine l : lastSelectedOrder.getItemsList()) {
                        if (!l.fulfill())
                            throw new IllegalStateException();
                    }

                    Order.updateOrderStatus(getCurrentSelectedOrderID(), Order.OrderStatus.FULFILLED);
                    db.commit();
                } catch (IllegalStateException error) {
                    try {
                        db.rollback();
                    } catch (SQLException ex) {
                        DatabaseBridge.databaseError("Could not clean up", ex);
                    }
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Could not fulfill order: Not enough stock held", "Error", JOptionPane.WARNING_MESSAGE);
                } catch (SQLException error) {
                    DatabaseBridge.databaseError("Could not fulfill order", error);
                    try {
                        db.rollback();
                    } catch (SQLException ex) {
                        DatabaseBridge.databaseError("Could not clean up", ex);
                    }
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Could not fulfill order: " + error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        db.setAutoCommit(true);
                    } catch (SQLException error) {
                        DatabaseBridge.databaseError("Could not clean up", error);
                    }
                    db.closeConnection();
                }
                AppContext.queueStoreReload = true;
                refreshData();
                resetState();
            }
        });

        gbc.gridy++;
        JButton deleteOrder = new JButton("Delete Order");
        panel.add(deleteOrder, gbc);
        deleteOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DatabaseBridge db = DatabaseBridge.instance();
                try {
                    db.openConnection();
                    PreparedStatement deleteOrder = db.prepareStatement("DELETE FROM `Order` WHERE orderId=?");
                    deleteOrder.setInt(1, getCurrentSelectedOrderID());
                    deleteOrder.executeUpdate();
                } catch (SQLException error) {
                    DatabaseBridge.databaseError("Could not delete order", error);
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Could not delete order: " + error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    db.closeConnection();
                }
                refreshData();
                resetState();
            }
        });

        gbc.gridy++;
        gbc.weighty = 1;
        JPanel filler = new JPanel();
        panel.add(filler, gbc);

        return panel;
    }

    private void resetState() {
        lastSelectedRow = -1;
        lastSelectedOrder = null;

        orderContents.setModel(new DefaultTableModel());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setEnabledRecursively(orderControls, false);
            }
        });

        revalidate();
        repaint();
    }

    private void refreshData() {
        orderViewContainer.removeAll();
        orderData = loadData();

        // Create a new JTable with the updated order data
        orderList = new JTable(new OrderViewTableModel(orderData, orderViewColumns));
        JScrollPane scrollPane = new JScrollPane(orderList);
        orderList.setRowHeight(24);

        // Set up a sorter for the JTable to allow sorting by date in ascending order
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(orderList.getModel());
        orderList.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>(1);
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        // Configure the GridBagConstraints for placing the JTable in the order view container
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        orderViewContainer.add(scrollPane, gbc);

        // Add a ListSelectionListener to the order list to handle row selection events
        orderList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (orderList.getSelectedRow() != -1 && orderList.getSelectedRow() != lastSelectedRow) {
                    lastSelectedRow = orderList.getSelectedRow();
                    updateSingleOrderView(getCurrentSelectedOrderID());
                    setEnabledRecursively(orderControls, true);
                }
            }
        });

        revalidate();
        repaint();
    }

    private int getCurrentSelectedOrderID() {
        return (Integer)orderList.getValueAt(lastSelectedRow, 0);
    }

    private void updateSingleOrderView(int orderID) {
        {
            for (Order o : orders) {
                if (o.getOrderId() == orderID) {
                    lastSelectedOrder = o;
                    break;
                }
            }
        }
        assert(lastSelectedOrder != null);

        List<OrderLine> ol = lastSelectedOrder.getItemsList();
        Object[][] orderMatrix = new Object[ol.size()][orderLineColumns.length];
        float totalCost = 0;
        try {
            DatabaseBridge.instance().openConnection();
            int i = 0;
            for (OrderLine l : ol) {
                Product p = l.getItem();
                orderMatrix[i][0] = p.getProductCode();

                if (p.isComponent()) {
                    orderMatrix[i][1] = p.getComponent().getBrand();
                } else {
                    orderMatrix[i][1] = "N/A";
                }

                orderMatrix[i][2] = p.getName();
                orderMatrix[i][3] = l.getQuantity();

                float subTotal = (float) (l.getQuantity() * p.getPrice());
                totalCost += subTotal;
                orderMatrix[i][4] = subTotal;

                i++;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Could not show order contents: " + e.getMessage(), "Internal Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseBridge.instance().closeConnection();
        }

        orderContents.setModel(new OrderViewTableModel(orderMatrix, orderLineColumns));
        orderContents.getColumnModel().getColumn(4).setCellRenderer(new CurrencyCellRenderer());

//        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(orderContents.getModel());
//        orderContents.setRowSorter(sorter);
//        List<RowSorter.SortKey> sortKeys = new ArrayList<>(1);
//        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
//        sorter.setSortKeys(sortKeys);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        orderTotal.setText("<html><b>Total:</b> <u>£" + decimalFormat.format(totalCost) + "</u></html>");

        revalidate();
        repaint();
    }

    private Object[][] loadData() {
        Object[][] orderData;

        // will need to query a customer payment and address for each order
        DatabaseBridge db = DatabaseBridge.instance();
        try {
            db.openConnection();
            orders = Order.getOrdersWithStatus(Order.OrderStatus.CONFIRMED);

            if (orders.isEmpty()) {
                return new Object[0][orderViewColumns.length];
            }
            orderData = new Object[orders.size()][orderViewColumns.length];

            for (int i = 0; i < orders.size(); i++) {
                Order o = orders.get(i);
                orderData[i][0] = o.getOrderId();
                orderData[i][1] = o.getDate().toString();

                Person p = Person.getPersonByID(o.getCustomerID());
                orderData[i][2] = p.getFullName();
                orderData[i][3] = p.getEmail();
                orderData[i][4] = p.getAddress().toString();

                if (p.getBankDetailsId() == -1) {
                    orderData[i][5] = "NONE";
                } else {
                    orderData[i][5] = "VALID";
                }

                orderData[i][6] = o.getStatus();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            db.closeConnection();
        }

        return orderData;
    }

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {

    }

    @Override
    public void onSelected() {
        refreshData();
        resetState();
    }
}
