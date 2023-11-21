package gui;

import entity.StoreAttributes;
import entity.user.Manager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.TableView;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class ManagerScreen extends JPanel implements TabbedGUIContainer.TabPanel {
    private TabbedGUIContainer parent;

    JLabel heading;
    JTable userTable;
    JScrollPane scrollPane;
    String[] roleNames = StoreAttributes.Role.getStringValues();

    public ManagerScreen() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        TableModel tableModel = new AbstractTableModel() {
            public int getRowCount() {
                return 100;
            }

            public int getColumnCount() { return 2; }

            public Object getValueAt(int rowIndex, int columnIndex) {
                return null;
            }

            @Override
            public String getColumnName(int column) {
                return switch (column) {
                    case 0 -> "User";
                    case 1 -> "Role";
                    default -> "Unknown";
                };
            }
        };


        heading = new JLabel("Manager Dashboard");
        userTable = new JTable(tableModel);
        scrollPane = new JScrollPane(userTable);

        add(heading);
        add(scrollPane);
    }

    public void addUser(String name, StoreAttributes.Role role) {
        JComboBox<String> comboBox = new JComboBox<>(roleNames);
        comboBox.setSelectedItem(role.toString());

        int tableIndex = userTable.getRowCount();

        userTable.add(new JLabel(name), tableIndex);
        userTable.add(comboBox, tableIndex);
    }

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {
        parent = cont;
    }
}
