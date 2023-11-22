package gui.person;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShopCard extends JFrame {

    private static JFrame frame;
    private BoxSet boxSet; // Store the BoxSet as an instance variable

    public ShopCard(Map<String, String> productInfo, String category) {
        // Create data for the table
        Object[][] data = new Object[productInfo.size() + 1][2];

        int i = 0;
        for (Map.Entry<String, String> entry : productInfo.entrySet()) {
            data[i][0] = entry.getKey();
            data[i][1] = entry.getValue();
            i++;
        }

        // Set the default value for "Quantity" to 0
        data[i][0] = "Quantity";
        data[i][1] = "0"; // Set the default value to 0

        // Create column names
        String[] columnNames = {"", ""};

        // Create a DefaultTableModel
        int finalI = i;
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 && row == finalI; // Make only the Quantity cell editable
            }
        };

        // Create a JTable with the model
        JTable table = new JTable(model);

        // Merge the cells in the first row
        table.setRowHeight(0, 2 * table.getRowHeight(0));

        // Add borders between rows
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        // Make the first merged row bold
        table.setFont(new Font("Arial", Font.PLAIN, 12));

        // Create a JPanel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);  // Add a scroll pane to the table

        panel.setPreferredSize(new Dimension(300, 200));  // Set the preferred size of the panel
        frame.add(panel);  // Add the panel to the frame

        // Create a BoxSet instance for TRACK_PACKS and TRAIN_SETS categories
        if (category.equals("TRACK_PACKS") || category.equals("TRAIN_SETS")) {
            boxSet = createBoxSet(productInfo);
        } else {
            boxSet = null;
        }

        // Create a JButton for "Add To Cart"
        JButton addToCartButton = new JButton("Add To Cart");
        addToCartButton.addActionListener(e -> {
            // Get the quantity from the table
            int quantity = Integer.parseInt((String) table.getValueAt(finalI, 1));

            // Get the product name from the table
            String productName = (String) table.getValueAt(0, 1);

            // Print the quantity and product name
            System.out.println("Quantity: " + quantity);
            System.out.println("Product Name: " + productName);
        });

        // Create a JButton for "Show More" only for TRACK_PACKS and TRAIN_SETS
        if (category.equals("TRACK_PACKS") || category.equals("TRAIN_SETS")) {
            JButton showMoreButton = new JButton("Show More");
            showMoreButton.addActionListener(e -> {
                // Display the box set information when "Show More" is pressed
                if (boxSet != null) {
                    showBoxSetDetails(boxSet); // Call a method to show the box set details
                } else {
                    JOptionPane.showMessageDialog(null, "No Box Set Information available.");
                }
            });

            // Create a JPanel for the buttons
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonsPanel.add(addToCartButton);
            buttonsPanel.add(showMoreButton);

            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            panel.add(buttonsPanel, BorderLayout.SOUTH); // Add the button panel at the bottom

            panel.setPreferredSize(new Dimension(300, 250)); // Adjusted preferred size
            frame.add(panel); // Add the content panel to the frame
        } else {
            // If not TRACK_PACKS or TRAIN_SETS, add only the "Add To Cart" button
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            panel.add(addToCartButton, BorderLayout.SOUTH);

            panel.setPreferredSize(new Dimension(300, 250));  // Adjusted preferred size
            frame.add(panel);  // Add the panel to the frame
        }
    }

    // Method to show the details of the BoxSet
    private void showBoxSetDetails(BoxSet boxSet) {
        // Create a new JFrame to display the products within the BoxSet
        JFrame boxSetFrame = new JFrame("Box Set Details");
        boxSetFrame.setLayout(new BorderLayout());

        // Create a JTextArea to display the products within the BoxSet
        JTextArea boxSetDetailsTextArea = new JTextArea(boxSet.toString());
        boxSetDetailsTextArea.setEditable(false);

        // Add the JTextArea to a JScrollPane for scrolling
        JScrollPane scrollPane = new JScrollPane(boxSetDetailsTextArea);

        // Add the JScrollPane to the JFrame
        boxSetFrame.add(scrollPane, BorderLayout.CENTER);

        // Set JFrame properties
        boxSetFrame.setSize(400, 300);
        boxSetFrame.setLocationRelativeTo(null);
        boxSetFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        boxSetFrame.setVisible(true);
    }

    // Create a BoxSet instance based on the product information
    private BoxSet createBoxSet(Map<String, String> productInfo) {
        BoxSet boxSet = new BoxSet();
        for (Map.Entry<String, String> entry : productInfo.entrySet()) {
            boxSet.addProduct(entry.getKey(), entry.getValue());
        }
        return boxSet;
    }

    // Define a BoxSet class to represent a set of products
    private static class BoxSet {
        private Map<String, String> products;

        public BoxSet() {
            products = new LinkedHashMap<>();
        }

        public void addProduct(String key, String value) {
            products.put(key, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : products.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            return sb.toString();
        }
    }

    private static class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Add borders between rows
            if (row > 0) {
                int topBorder = 1;
                int bottomBorder = 1;
                ((JComponent) component).setBorder(BorderFactory.createMatteBorder(
                        topBorder, 0, bottomBorder, 0, Color.BLACK));
            }

            if (row == 0) {
                component.setFont(new Font("Arial", Font.BOLD, 12));
            }

            return component;
        }
    }

    public static void main(String[] args) {
        // Set up the JFrame
        frame = new JFrame("ShopCard");
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        // Example usage with combined information
        Map<String, String> productInfo1 = new LinkedHashMap<>();  // Use LinkedHashMap
        productInfo1.put("Product Name", "Example Product 1");
        productInfo1.put("Price", "£19.99");

        Map<String, String> productInfo2 = new LinkedHashMap<>();  // Use LinkedHashMap
        productInfo2.put("Product Name", "Example Product 2");
        productInfo2.put("Price", "£29.99");
        productInfo2.put("Brand", "Example Brand 2");
        productInfo2.put("Era", "2023");
        productInfo2.put("Gauge", "HO");
        productInfo2.put("PriceBracket", "DIGITAL");
        productInfo2.put("ControlType", "DIGITAL");
        productInfo2.put("Curvature", "Example Curvature 2");

        SwingUtilities.invokeLater(() -> {
            new ShopCard(productInfo1, "TRACK_PACKS");
            new ShopCard(productInfo2, "LOCOMOTIVES");
        });
    }
}
