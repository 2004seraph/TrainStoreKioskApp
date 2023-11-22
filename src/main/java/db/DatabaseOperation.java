package db;

import controllers.AppContext;
import entity.order.OrderLine;
import entity.user.*;
import entity.*;
import entity.StoreAttributes.Role;
import entity.order.Order;
import utils.Crypto;

import java.security.InvalidKeyException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;

/**
 * Maintainers: Sam Taseff
 * <br>
 * This is a static class encapsulating all database operations, it needs its
 * connection initialized before any other calls, this is done with SetConnection(DatabaseBridge conn)
 */
public final class DatabaseOperation {

    /**
     * Maintainers: Sam Taseff
     * <br>
     * Inheriting from this class allows children to run their own SQL statements
     * using the inherited BeginQuery method - it connects to the same top-level
     * database connection reference as DatabaseOperations.
     * <br><br>
     * It isn't needed for anything currently, it's here purely in case we need it.
     */
    public static abstract class Entity {
        public static void openConnection() throws SQLException { db.openConnection(); }
        public static void closeConnection() { db.closeConnection(); }
        public static PreparedStatement prepareStatement(String sql) throws SQLException {
            return db.prepareStatement(sql);
        }
        public static PreparedStatement prepareStatement(String sql, int opts) throws SQLException {
            return db.prepareStatement(sql, opts);
        }
        public static void commit() throws SQLException { db.commit(); }
        public static void rollback() throws SQLException { db.rollback(); }
        public static void setAutoCommit(boolean status) throws SQLException { db.setAutoCommit(status); }
    }

    private static DatabaseBridge db = null;

    /**
     * This class should not be instantiated
     */
    private DatabaseOperation() { }

    /**
     * Sets the database connection on which these actions affect
     * @param conn A database connection bridge
     */
    public static void SetConnection(DatabaseBridge conn) {
        db = conn;
    }

    /**
     * Update the stock level of a product given its product code
     * @param productCode product to update
     * @param newStock new stock level
     * @return whether operation was successful or not
     * @throws SQLException
     */
    public static boolean UpdateStock(String productCode, int newStock) throws SQLException {
        try (PreparedStatement query = db.prepareStatement("UPDATE Product SET stockLevel = ? WHERE productCode = ?")) {
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
     * Finds user by ID and updates their staff role
     * @param userId primary key of the user
     * @param newRole Either USER, STAFF or MANAGER
     * @return whether operation was successful
     * @throws SQLException
     */
    public static boolean UpdateUserRoleById(int userId, StoreAttributes.Role newRole) throws SQLException {
        try (PreparedStatement findQuery = db.prepareStatement("SELECT * FROM Person WHERE PersonId = ?")) {
            findQuery.setInt(1, userId);
            ResultSet rs = findQuery.executeQuery();

            if (!rs.next()) {
                throw new Person.PersonNotFoundException("Failed to find person with id ["+userId+"]");
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to find person with id ["+userId+"]", e);
            throw e;
        }

        try(PreparedStatement roleQuery = db.prepareStatement("UPDATE Role SET role = ? WHERE personId = ?")) {
            roleQuery.setString(1, newRole.toString());
            roleQuery.setInt(2, userId);

            return roleQuery.executeUpdate() > 0;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to update user with id ["+userId+"] to role ["+newRole.toString()+"]", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        DatabaseBridge db = DatabaseBridge.instance();
        DatabaseOperation.SetConnection(db);
        try{
            db.openConnection();
            BankDetail.CreatePaymentInfo("1234567890123456", Date.valueOf("2021-01-01"), "123");
//            BankDetail bank = GetBankDetailsById(2);
//            System.out.println(bank);
        } catch (Exception e) {
            System.out.println("Failed to open connection");
            System.out.println(e.getMessage());
        } finally {
            db.closeConnection();
        }
    }
}
