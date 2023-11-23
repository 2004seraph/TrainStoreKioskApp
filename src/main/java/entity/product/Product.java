package entity.product;

import db.DatabaseBridge;
import db.DatabaseOperation;
import db.DatabaseRecord;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Product extends DatabaseOperation.Entity implements DatabaseRecord {

    protected String productCode;
    protected String name;
    protected Integer stockLevel;
    protected Double price;

    public String getProductCode() {
        return productCode;
    }
    public String getName() {
        return name;
    }
    public Integer getStockLevel() {
        return stockLevel;
    }
    public Double getPrice() {
        return price;
    }

    public Product(String name, int stock, Double price) {
        this.name = name;
        this.stockLevel = stock;
        this.price = price;
    }

    private Product(String productCode, String name, int stock, Double price) {
        this.productCode = productCode;
        this.name = name;
        this.stockLevel = stock;
        this.price = price;
    }

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

    /**
     * Returns a Product instance from the database using a product code
     * @param productCode The string product code
     * @return An instance of Product containing the field data
     * @throws SQLException
     */
    public static Product getProductByID(String productCode) throws SQLException {
        try (PreparedStatement query = prepareStatement("SELECT * FROM Product WHERE productCode = ?")) {
            query.setString(1, productCode);

            ResultSet res = query.executeQuery();
            if (res.next()) {
                return new Product(
                        res.getString("productCode"),
                        res.getString("name"),
                        res.getInt("stockLevel"),
                        res.getDouble("price")
                );
            } else {
                throw new IllegalArgumentException("Product ID does not exist");
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to update stock with product code ["+productCode+"]", e);
            throw e;
        }
    }

    /**
     * Gets all the products to display in a view
     * @return ResultSet of all products or null if there are no products
     * @throws SQLException
     */
    public static ResultSet getAllProducts() throws SQLException {
        try {
            PreparedStatement productsQuery = prepareStatement("SELECT * FROM Product");
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

    @Override
    public String toString() {
        return "[Product " + productCode + " -> { Name: " + name + ", Stock: " + stockLevel + ", Price: " + price + " }]";
    }

    public static void main(String[] args) { // FUNCTIONAL
        DatabaseOperation.setConnection(DatabaseBridge.instance());

        Product thing = null;
        try {
            openConnection();
            thing = getProductByID("S1234");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        assert thing != null;
        System.out.println(thing);
    }

    @Override
    public List<Object> getFields() {
        return Arrays.asList(
                name,
                stockLevel,
                price
        );
    }
}

