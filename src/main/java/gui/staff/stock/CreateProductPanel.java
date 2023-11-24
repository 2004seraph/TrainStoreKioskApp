package gui.staff.stock;

import static utils.GUI.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class CreateProductPanel extends JPanel {
    JTextField productCodeInput;
    JTextField nameInput;
    JFormattedTextField stockInput;
    JFormattedTextField priceInput;

    public CreateProductPanel() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        this.setLayout(gbl);

        JPanel productData = createProductDataPanel(); // common product data
        JPanel productType = createProductTypePanel(); // either component data OR boxedSetContentList
        JPanel componentType = createComponent();      // either specific component type data OR product lookup for boxedSet

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(productData, gbc);
        gbc.gridx = 1;
        add(productType, gbc);
        gbc.gridx = 2;
        add(componentType, gbc);
    }

    private JPanel createProductDataPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
//        panel.setBackground(Color.CYAN);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("<html><h2>Product Information</h2></html>");
        title.setBorder(new EmptyBorder(0,6,0,0));
        panel.add(title, gbc);
        {
            JLabel productCodeLabel = new JLabel("Product Code");
            productCodeInput = new JTextField();
            JPanel container = createLabelInputRow(productCodeLabel, productCodeInput);
            gbc.gridy = 1;
            panel.add(container, gbc);
        }
        {
            JLabel nameLabel = new JLabel("Name");
            nameInput = new JTextField();
            JPanel container = createLabelInputRow(nameLabel, nameInput);
            gbc.gridy = 2;
            panel.add(container, gbc);
        }
        {
            JLabel stockLabel = new JLabel("Initial Stock Level");
            stockInput = new JFormattedTextField(getIntegerFormatter());
            JPanel container = createLabelInputRow(stockLabel, stockInput);
            gbc.gridy = 3;
            panel.add(container, gbc);
        }
        {
            JLabel priceLabel = new JLabel("Price per Unit");
            priceInput = new JFormattedTextField(getCurrencyFormatter());
            JPanel container = createLabelInputRow(priceLabel, priceInput);
            gbc.gridy = 4;
            panel.add(container, gbc);
        }
        {
            gbc.weighty = 1;
            gbc.gridy = 5;
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            panel.add(filler, gbc);
        }

        return panel;
    }

    private JPanel createProductTypePanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.PINK);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("<html><h2>Product Type</h2></html>");
        title.setBorder(new EmptyBorder(0,6,0,0));
        panel.add(title, gbc);

        {
            gbc.weighty = 1;
            gbc.gridy = 5;
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            panel.add(filler, gbc);
        }

        return panel;
    }

    private JPanel createComponent() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel componentType = new JPanel();
        componentType.setLayout(new GridBagLayout());
        componentType.setBackground(Color.MAGENTA);

        return componentType;
    }
}
