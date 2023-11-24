package gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;

    private int row;
    private int column;

    private final ButtonColumn.TextFunction textFunction;
    private final ButtonColumn.ActionFunction actionFunction;

    public ButtonEditor(ButtonColumn.TextFunction textFunction, ButtonColumn.ActionFunction actionFunction) {
        super(new JCheckBox());

        button = new JButton();
        button.setOpaque(true);

        this.textFunction = textFunction;
        this.actionFunction = actionFunction;

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
//            System.out.println("set: " + row);
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }

        isPushed = true;

        button.setText(this.textFunction.setText(row, column)); // when clicked

        this.row = row;
        this.column = column;

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            this.actionFunction.onClick(this.row, this.column);
        }

        isPushed = false;

        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;

        return super.stopCellEditing();
    }
}