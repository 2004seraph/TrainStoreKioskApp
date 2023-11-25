package utils;

import gui.TabbedGUIContainer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;
import javax.swing.text.NumberFormatter;

public final class GUI {
    private GUI() {}

    public static final NumberFormat ukCurrencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "GB"));

    public static NumberFormatter getIntegerFormatter() {
        NumberFormat format = NumberFormat.getIntegerInstance();
//        format.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(format);
        numberFormatter.setValueClass(Long.class);
        numberFormatter.setAllowsInvalid(false); //this is the key

        return numberFormatter;
    }

    public static NumberFormatter getCurrencyFormatter() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
//        format.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(format);
        numberFormatter.setValueClass(Double.class);
        numberFormatter.setAllowsInvalid(false); //this is the key

        return numberFormatter;
    }

    /**
     * Creates a neat row element with a label on the left and an input on the right, you can then stack these vertically
     * @param label
     * @param input
     * @return A JPanel containing the layout of the label and input
     */
    public static JPanel createLabelInputRow(JLabel label, Component input) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
//        panel.setBackground(Color.DARK_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();

        Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.LIGHT_GRAY, Color.GRAY);
        panel.setBorder(raisedEtched);

        label.setOpaque(true);
        label.setBorder(new EmptyBorder(0,10,0,0));
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);

        gbc.weightx = 0;
        gbc.gridx = 1;

        if (input instanceof JTextField) {
            ((JTextField)input).setColumns(16);
            ((JTextField)input).setBorder(new EmptyBorder(3, 7, 3, 3));
        }
        panel.add(input, gbc);

        return panel;
    }

    public static void setEnabledRecursively(Component panel, boolean status) {
        panel.setEnabled(status);
        for (Component cp : ((Container)panel).getComponents()) {
            setEnabledRecursively(cp, status);
        }
    }

    /**
     * Returns the selected button from a button group (radio buttons)
     * @param buttonGroup Your set of radio buttons
     * @return The text of the selected button, or null
     */
    public static String getSelectedButtonFromGroup(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }

        return null;
    }
}
