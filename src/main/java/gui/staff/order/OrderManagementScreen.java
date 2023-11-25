package gui.staff.order;

import db.DatabaseBridge;
import entity.Address;
import entity.order.Order;
import entity.product.Product;
import entity.user.Person;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class OrderManagementScreen extends JPanel {
    private final String[] columns = new String[]{"Order ID", "Date", "Customer", "Address", "Payment", "Status"};

    private Object[][] orderData;

    private JPanel orderViewContainer;

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

        JLabel infoLabel = new JLabel("Click any order to view its details and perform operations.");
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
        orderViewContainer = new JPanel();
        orderViewContainer.setLayout(new GridBagLayout());
        add(orderViewContainer, gbc);
        refreshData();

        gbc.weighty = 1;
        gbc.gridy++;
        add(createOrderControls(), gbc);

//        gbc.weighty = 1;
//        gbc.gridy++;
//        {
//            JPanel filler = new JPanel();
//            add(filler, gbc);
//        }
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



        { // filler
            gbc.weighty = 1;
            gbc.gridy++;
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            panel.add(filler, gbc);
        }

        return panel;
    }

    private void refreshData() {
        orderViewContainer.removeAll();
        orderData = loadData();
        JTable jt = new JTable(new OrderViewTableModel(orderData, columns));
        JScrollPane scrollPane = new JScrollPane(jt);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        orderViewContainer.add(scrollPane, gbc);
    }

    private Object[][] loadData() {
        Object[][] orderData;
        List<Order> orders;

        // will need to query a customer payment and address for each order
        DatabaseBridge db = DatabaseBridge.instance();
        try {
            db.openConnection();
            orders = Order.getOrdersWithStatus(Order.OrderStatus.CONFIRMED, Order.OrderStatus.FULFILLED);

            if (orders.isEmpty()) {
                return new Object[0][columns.length];
            }
            orderData = new Object[orders.size()][columns.length];

            for (int i = 0; i < orders.size(); i++) {
                Order o = orders.get(i);
                orderData[i][0] = o.getOrderId();
                orderData[i][1] = o.getDate().toString();
                orderData[i][2] = o.getCustomerID();

//                Person p = Person.getPersonByID(o.getCustomerID());
//                orderData[i][3] = p.getAddress().toString();
//                orderData[i][4] = p.getBankDetail().toString();

                orderData[i][5] = o.getStatus();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            db.closeConnection();
        }

        return orderData;
    }
}
