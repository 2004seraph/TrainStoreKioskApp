package gui.staff.stock;

import db.DatabaseBridge;

import javax.swing.table.AbstractTableModel;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class StockManagementTableModel extends AbstractTableModel {

    private Object[][] productData;
    private String[] columns;

    public StockManagementTableModel(Object[][] productData, String[] columns) {
        this.productData = productData;
        this.columns = columns;
    }

    @Override
    public int getRowCount() {
        return this.productData.length;
    }

    @Override
    public int getColumnCount() {
        return this.columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.productData[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int columnIndex) {
        return this.columns[columnIndex];
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        DatabaseBridge db = DatabaseBridge.instance();
        PreparedStatement update;
        try {
            db.openConnection();
            switch (columnIndex) {
                case 1: // name
                    update = db.prepareStatement("UPDATE Product SET name=? WHERE productCode=?");
                    update.setString(1, (String)value);
                    break;
                case 2: // price
                    update = db.prepareStatement("UPDATE Product SET price=? WHERE productCode=?");
                    update.setDouble(1, (double)value);
                    break;
                case 3: // stock
                    update = db.prepareStatement("UPDATE Product SET stockLevel=? WHERE productCode=?");
                    update.setInt(1, (int)value);
                    break;
                default:
                    return;
            }

            update.setString(2, (String)productData[rowIndex][0]);
            update.executeUpdate();
            this.productData[rowIndex][columnIndex] = value;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Could not edit stock", e);
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return ! (columnIndex == 0);// || columnIndex == 4);
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return String.class;
            case 1: return String.class;
            case 2: return Double.class;
            case 3: return Integer.class;
            default: return String.class;
        }
    }
}