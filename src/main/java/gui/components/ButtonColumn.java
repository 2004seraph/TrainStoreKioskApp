package gui.components;

import javax.swing.*;
import javax.swing.table.TableColumn;

public final class ButtonColumn {
    public interface TextFunction {
        public String setText(int row, int column);
    }

    private ButtonColumn() { }

    public static void setButtonColumn(TableColumn column, TextFunction textFunction) {
        column.setCellRenderer(new ButtonRenderer(textFunction));
        column.setCellEditor(new ButtonEditor(new JCheckBox())); // weird swing
    }
}
