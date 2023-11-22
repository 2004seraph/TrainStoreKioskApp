package entity.product;

import db.DatabaseBridge;
import db.DatabaseOperation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Product extends DatabaseOperation.Entity {

    private Integer productCode;
    private String name;
    protected Integer stockLevel;
    private Double price;


    /**
     * Update the stock level of a product given its product code
     * @param productCode product to update
     * @param newStock new stock level
     * @return whether operation was successful or not
     * @throws SQLException
     */
    public static boolean updateStock(String productCode, int newStock) throws SQLException {
        try (PreparedStatement query = prepareStatement("UPDATE Product SET stockLevel = ? WHERE productCode = ?")) {
            query.setInt(1, newStock);
            query.setString(2, productCode);

            int updatedRows = query.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to update stock with product code ["+productCode+"]", e);
            throw e;
        }
    }

//    public static Product getProductByID(String productCode) throws SQLException {
//
//    }

    /**
     * Gets all the products to display in a view
     * @return ResultSet of all products or null if there are no products
     * @throws SQLException
     */
    public static ResultSet getProducts() throws SQLException {
        try (PreparedStatement productsQuery = prepareStatement("SELECT * FROM Product")) {
            ResultSet results = productsQuery.executeQuery();
            if (!results.next()) {
                return null;
            }
            return results;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to fetch products", e);
            throw e;
        }
    }
}

