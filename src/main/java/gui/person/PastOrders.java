package gui.person;

import entity.order.Order.OrderStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class PastOrders extends JPanel {

    private int orderId;
    private OrderStatus status;
    private Date orderDate;
    private boolean isExpanded; // Track whether the order is expanded or not
    private Map<String, PastOrderItem> pastOrderItems;

    public PastOrders() {
    }

    public void createOrderLine(Map<String, PastOrderItem> PastOrderItems) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
        // Create a JPanel for order ID, status, and date
        JPanel orderInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        orderInfoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    
        // Add order ID, status, and date to the orderInfoPanel
        JLabel orderIdStatusLabel = new JLabel("#" + orderId + " (" + status + ") - " + orderDate);
        orderInfoPanel.add(orderIdStatusLabel);
    
        // Add the "View More/View Less" button
        JButton viewToggleButton = new JButton(isExpanded ? "View Less" : "View More");
        viewToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleView(); // Toggle between full and less details when the button is clicked
            }
        });
        orderInfoPanel.add(viewToggleButton);
    
        // Add the orderInfoPanel to the main panel (this)
        add(orderInfoPanel);
    
        if (isExpanded) {
            // Only show detailed information if the order is expanded
            addDetailedOrderInfo(PastOrderItems);
        }
    
        // Store the past order items for later use
        pastOrderItems = PastOrderItems;
    
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this); // Get the parent frame
        if (frame != null) {
            frame.pack(); // Adjust frame size based on content
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }
    }    

    private void addDetailedOrderInfo(Map<String, PastOrderItem> PastOrderItems) {
        // Create a DefaultTableModel with column names and 0 rows
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Product Name", "Quantity", "Unit Price", "Total"}, 0);

        double orderTotal = 0; // Variable to store the total order price

        // Add past order items to the table model
        for (Map.Entry<String, PastOrderItem> entry : PastOrderItems.entrySet()) {
            PastOrderItem PastOrderItem = entry.getValue();
            Object[] rowData = {
                    PastOrderItem.getProductName(),
                    PastOrderItem.getQuantity(),
                    PastOrderItem.getUnitPrice(),
                    PastOrderItem.getTotal()
            };
            model.addRow(rowData);

            // Add the item's total to the order total
            orderTotal += PastOrderItem.getTotal();
        }

        // Add a row for the total price
        model.addRow(new Object[]{"Total", "", "", formatCurrency(orderTotal)});

        // Create the JTable with the model
        JTable pastOrdersTable = new JTable(model);

        // Create a JScrollPane for scrolling
        JScrollPane scrollPane = new JScrollPane(pastOrdersTable);

        // Set the preferred size of the JScrollPane based on the content
        Dimension preferredSize = scrollPane.getPreferredSize();
        scrollPane.setPreferredSize(new Dimension(preferredSize.width, preferredSize.height));

        // Add the JScrollPane to the main panel
        add(scrollPane, BorderLayout.CENTER);
    }

    // Helper method to format currency
    private String formatCurrency(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return "£" + decimalFormat.format(amount);
    }

    // Helper method to toggle between full and less details
    private void toggleView() {
        removeAll(); // Remove all components from the panel

        // Toggle the state
        isExpanded = !isExpanded;

        // Recreate the order info panel with the stored past order items
        createOrderLine(pastOrderItems);
    }

    /**
     * Represents an item in a past order, containing information such as the product name,
     * quantity, and unit price. Provides methods to retrieve individual details and calculate
     * the total cost of the item.
     */
    public class PastOrderItem {
        private String productName;
        private int quantity;
        private String unitPrice;

        public PastOrderItem(String productName, int quantity, String unitPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getUnitPrice() {
            return unitPrice;
        }

        public double getTotal() {
            return quantity * parseUnitPrice(unitPrice);
        }

        // Helper method to parse unit price as a double
        private static double parseUnitPrice(String unitPrice) {
            return Double.parseDouble(unitPrice.replaceAll("[^\\d.]", ""));
        }
    }
}
