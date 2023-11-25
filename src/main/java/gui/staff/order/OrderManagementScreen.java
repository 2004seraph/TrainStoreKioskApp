package gui.staff.order;

import javax.swing.*;
import java.awt.*;

public class OrderManagementScreen extends JPanel {
    private final String[] columns = new String[]{"Order ID", "Date", "Customer", "Address", "Payment", "Status"};

    public OrderManagementScreen() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        this.setLayout(gbl);

        // jtable with all orders, status screen below which displays the full order when the row is selected
        // basic filters at the top

        JTable jt = new JTable();
        JScrollPane scrollPane = new JScrollPane(jt);
        add(scrollPane, gbc);
    }
}
