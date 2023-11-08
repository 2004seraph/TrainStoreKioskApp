package db;

import java.util.List;

/**
 * Maintainers: Sam Taseff
 * <br>
 * If you want your class to be insertable into the database and 'getable'
 * (if it represents a record in the db), then you must implement this interface
 */
public interface DatabaseRecord {
    /**
     * This function will allow the DatabaseOperations class to extract the correct record information from this object.
     * <br><br>
     * Note that you SHOULD NOT include the primary key in this (that is made by MySQL)
     * @return A list of the correctly ordered (and correctly typed) table field values for this entity, excluding the primary key
     */
    public List<Object> GetFields();

    /**
     * This function allows the DatabaseOperations class to create an instance of this
     * class and fill the correct members with the field data from a row in the table
     * @param fields A list of the values of each column, including the primary key
     */
    //public void SetFields(List<Object> fields);
}
