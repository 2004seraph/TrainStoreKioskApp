package utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.text.NumberFormat;
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
    public static JPanel createLabelInputRow(JLabel label, JTextField input) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.DARK_GRAY);
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
        input.setColumns(16);
        input.setBorder(new EmptyBorder(2, 7, 2, 2));
        panel.add(input, gbc);

        return panel;
    }
}
