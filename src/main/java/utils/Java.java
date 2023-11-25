package utils;

import java.util.Calendar;
import java.util.Date;

public final class Java {
    private Java() {}

    /**
     * Removes a column from a 2D array
     * @param array A 2D array
     * @param colRemove The index of the column to be removed, starting from 0
     * @return A new 2D array
     */
    public static Object[][] removeMatrixColumn(Object[][] array, int colRemove)
    {
        int row = array.length;
        int col = array[0].length;

        Object[][] newArray = new Object[row][col-1];

        for (int i = 0; i < row; i++) {
            for (int j = 0, currColumn = 0; j < col; j++) {
                if (j != colRemove) {
                    newArray[i][currColumn++] = array[i][j];
                }
            }
        }
        return newArray;
    }

    /**
     * Sets a specified column of a matrix to a certain value
     * @param array
     * @param column
     * @param value
     * @return
     */
    public static Object[][] setMatrixColumn(Object[][] array, int column, Object value)
    {
        int row = array.length;
        int col = array[0].length;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (j == column) {
                    array[i][j] = value;
                }
            }
        }
        return array;
    }

    /**
     * Creates a date object WITHOUT USING DEPRECATED APIS
     * @param day
     * @param month Can be something like Calendar.JANUARY
     * @param year
     * @return
     */
    public static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }
}
