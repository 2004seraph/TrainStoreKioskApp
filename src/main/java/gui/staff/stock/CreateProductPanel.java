package gui.staff.stock;

import javax.swing.*;
import java.awt.*;

public class CreateProductPanel extends JPanel {
    public CreateProductPanel() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        this.setLayout(gbl);
        this.setBackground(Color.CYAN);
        this.add(new JButton("hello"));
    }
}
