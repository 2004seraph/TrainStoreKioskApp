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
     * Gets all the products to display in a view
     * @return ResultSet of all products or null if there are no products
     * @throws SQLException
     */
    public static ResultSet GetProducts() throws SQLException {
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

