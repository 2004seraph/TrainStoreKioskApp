package gui.staff.stock;

import javax.swing.table.AbstractTableModel;
class BoxedSetConstructionTableModel extends AbstractTableModel {

    private Object[][] productData;
    private String[] columns;

    public BoxedSetConstructionTableModel(Object[][] productData, String[] columns) {
        super();
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
        productData[rowIndex][columnIndex] = value;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 2) {
            return Integer.class;
        } else {
            return String.class;
        }
    }
}