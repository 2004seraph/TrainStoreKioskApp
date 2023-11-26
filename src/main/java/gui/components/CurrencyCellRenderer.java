package gui.components;

import javax.swing.table.DefaultTableCellRenderer;

import static utils.GUI.ukCurrencyFormat;

public class CurrencyCellRenderer extends DefaultTableCellRenderer {
    @Override
    public void setValue(Object value)
    {
        if (value != null) {
            value = ukCurrencyFormat.format(value);
        }
        super.setValue(value);
    }
}