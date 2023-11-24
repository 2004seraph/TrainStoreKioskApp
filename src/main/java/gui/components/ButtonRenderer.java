package gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

class ButtonRenderer extends JButton implements TableCellRenderer {
	private ButtonColumn.TextFunction textFunction;

	public ButtonRenderer(ButtonColumn.TextFunction textFunction) {
		setOpaque(true);

		this.textFunction = textFunction;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(UIManager.getColor("Button.background"));
		}

		setText(this.textFunction.setText(row, column)); // shows unclicked
		return this;
	}
}

