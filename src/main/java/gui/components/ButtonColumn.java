package gui.components;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionListener;

public final class ButtonColumn {
    public interface TextFunction {
        public String setText(int row, int column);
    }
    public interface ActionFunction {
        public void onClick(int row, int column);
    }

    private ButtonColumn() { }

    public static void setButtonColumn(TableColumn column, TextFunction textFunction, ActionFunction buttonAction) {
        column.setCellRenderer(new ButtonRenderer(textFunction));
        column.setCellEditor(new ButtonEditor(textFunction, buttonAction)); // weird swing
    }
}
