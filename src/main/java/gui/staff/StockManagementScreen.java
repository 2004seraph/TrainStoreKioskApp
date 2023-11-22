package gui.staff;

import controllers.AppContext;

import javax.swing.*;
import java.awt.*;

public class StockManagementScreen extends JPanel {
    private static class StockLine extends JPanel {
        public String productID = null;

        public StockLine(String productID) {
            this.productID = productID;

            GridLayout gridLayout = new GridLayout(0,3);
            JPanel content = new JPanel();
            content.setLayout(gridLayout);
            add(content);

            content.add(new JLabel("speemo"));
            content.add(new JSeparator(SwingConstants.VERTICAL));
            content.add(new JLabel("speemo2"));
        }
    }

    public StockManagementScreen() {
        add(new StockLine(""));
    }

    public static void main(String[] args) {
        JFrame win = AppContext.getWindow();
        win.add(new StockManagementScreen());
        win.setVisible(true);
    }
}
