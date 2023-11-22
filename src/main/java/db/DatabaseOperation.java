package db;

import entity.user.*;
import entity.*;

import java.sql.*;

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
    public static void setConnection(DatabaseBridge conn) {
        db = conn;
    }

    /**
     * Finds user by ID and updates their staff role
     * @param userId primary key of the user
     * @param newRole Either USER, STAFF or MANAGER
     * @return whether operation was successful
     * @throws SQLException
     */
    public static boolean updateUserRoleById(int userId, StoreAttributes.Role newRole) throws SQLException {
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
        DatabaseOperation.setConnection(db);
        try{
            db.openConnection();
            BankDetail.createPaymentInfo("1234567890123456", Date.valueOf("2021-01-01"), "123");
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
