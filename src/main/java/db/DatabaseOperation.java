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
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return db.prepareStatement(sql);
        }
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
     * Inserts a new address into the database
     * @param address A non-null address that doesn't have any null fields either
     * @return Whether the insertion was successful or failed due to someone already having that house address
     * @throws SQLException
     */
    public static Boolean CreateAddress(Address address) throws SQLException {
        // Check if this address already exists using the primary key houseNumber and postCode

        try (PreparedStatement s = db.prepareStatement("SELECT * FROM Address WHERE houseNumber=? AND postCode=?")){
            Object [] fields = address.GetFields().toArray();

            s.setString(1, fields[0].toString());
            s.setString(2, fields[3].toString());

            ResultSet res = s.executeQuery();
            if(!res.next()){
                // Create a new address to the database
                try (PreparedStatement r = db.prepareStatement("INSERT INTO Address VALUES (?,?,?,?)")){
                    r.setString(1, fields[0].toString());
                    r.setString(2, fields[1].toString());
                    r.setString(3, fields[2].toString());
                    r.setString(4, fields[3].toString());
                    r.executeUpdate();
                } catch (SQLException e) {
                    DatabaseBridge.databaseError("Failed to insert new address");
                    throw e;
                }
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to insert new address");
            throw e;
        }
        return true;
    }

    /**
     * Inserts a Person object into the database
     * @param person A non-null person that doesn't have any null fields either
     * @return Whether the insertion was successful or failed due to someone already having that email address
     * @throws SQLException
     */
    public static Boolean CreatePerson(Person person) throws SQLException {
        // do not insert this person if an account with their email already exists
        if (GetPersonByEmail(person.getEmail()) != null) {
            return false;
        }

        db.setAutoCommit(false);
        int id = -1;

        try (PreparedStatement s = db.prepareStatement("INSERT INTO Person VALUES (default,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement r = db.prepareStatement("INSERT INTO Role VALUES (?,?)");
             ) {

            Object[] fields = person.GetFields().toArray();

            s.setString(1, (String) fields[0]); // forename
            s.setString(2, (String) fields[1]); // surname
            s.setString(3, (String) fields[2]); // email
            s.setString(4, (String) fields[3]); // password
            s.setString(5, (String) fields[4]); // housename
            s.setString(6, (String) fields[5]); // postcode
            if ((Integer)fields[6] != -1) {                  // paymentid
                s.setInt(7, (Integer) fields[6]);
            } else {
                s.setNull(7, Types.INTEGER);
            }
            s.executeUpdate();

            ResultSet rs = s.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                throw new InternalError("Failed to insert into Person table");
            }

            r.setInt(1, id);
            r.setString(2, person.getRole().name());
            r.executeUpdate();

            db.commit();
        } catch (SQLException | InternalError e) {
            DatabaseBridge.databaseError("Failed to insert new user", e);
            db.rollback();
            throw e;
        } finally {
            db.setAutoCommit(true);
        }

        return true;
    }

    /**
     * Gets a person from the database using their email
     * @param email A string of an email, can be valid or invalid
     * @return A Person object with all of its fields set, or null if there was no one with that email
     * @throws SQLException
     */

    public static Person GetPersonByEmail(
            String email
    ) throws SQLException {
        db = DatabaseBridge.instance(); // TODO:Im sure this isn't supposed to be here, but this line solves the problem
        try (PreparedStatement personQuery = db.prepareStatement("SELECT * FROM Person WHERE email=?");
             PreparedStatement roleQuery = db.prepareStatement("SELECT * FROM Role WHERE personId=?");
        ) {
            personQuery.setString(1, email);
            ResultSet res = personQuery.executeQuery();

            Person person;
            if (res.next()) {
                int id = res.getInt(1);
                roleQuery.setInt(1, id);
                ResultSet roles = roleQuery.executeQuery();

                Role userRole = Role.USER;
                // get the highest priviledge role this user has and use that
                while (roles.next()) {
                    Role roleValue = Role.valueOf(roles.getString(2));
                    if (roleValue.getLevel() > userRole.getLevel())
                        userRole = roleValue;
                }

                person = new Person(
                        id,                            // id
                        res.getString(2),   // forename
                        res.getString(3),   // surname
                        res.getString(4),   // email
                        res.getString(5),   // password (this is horrible)
                        res.getString(6),   // houseName
                        res.getString(7),   // postcode
                        res.getInt(8),      // bank details
                        userRole
                );

                return person;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Gets all the products to display in a view
     * @return ResultSet of all products or null if there are no products
     * @throws SQLException
     */
    public static ResultSet GetProducts() throws SQLException {
        try (PreparedStatement productsQuery = db.prepareStatement("SELECT * FROM Product")) {
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
     * Update the status of an order
     * @param orderId primary key of the order
     * @param newStatus should be either CONFIRMED, PENDING or FULFILLED
     * @return whether operation was successful
     * @throws SQLException
     */
    public static boolean UpdateOrderStatus(int orderId, Order.OrderStatus newStatus) throws SQLException {
        try (PreparedStatement findQuery = db.prepareStatement("SELECT * FROM Order WHERE orderId = ?")) {
            findQuery.setInt(1, orderId);
            ResultSet rs = findQuery.executeQuery();

            if (!rs.next()) {
                throw new Order.OrderNotFoundException("Failed to find order with orderId ["+orderId+"]");
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to find order with orderId ["+orderId+"]", e);
            throw e;
        }

        try (PreparedStatement query = db.prepareStatement("UPDATE Order SET status = ? WHERE orderId = ?")) {
            query.setString(1, newStatus.toString());
            query.setInt(2, orderId);

            int rows = query.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to update order with orderId ["+orderId+"] to status ["+newStatus+"]", e);
            throw e;
        }
    }

    // a person can update their personal info

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

    /**
     * Assuming you already have the person entity you can update their role
     * @param person Person entity
     * @param newRole Either USER, STAFF or MANAGER
     * @return whether operation was successful
     * @throws SQLException
     */
    public static boolean UpdateUserRole(Person person, StoreAttributes.Role newRole) throws SQLException {
        try (PreparedStatement query = db.prepareStatement("""
                UPDATE Role, Person
                LEFT JOIN Role R ON Person.PersonId = R.personId
                SET R.role = ?
                WHERE email = ?
                """)) {
            query.setString(1, newRole.toString());
            query.setString(2, person.getEmail());

            return query.executeUpdate() > 0;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to update user with email ["+person.getEmail()+"] to role ["+newRole.toString()+"]", e);
            throw e;
        }
    }

    /**
     * Get the order with its items
     * @param orderId Primary key
     * @return Order entity with order lines
     * @throws SQLException
     */
    public static Order GetOrderWithOrderLine(Integer orderId) throws SQLException {
        try (PreparedStatement findQuery = db.prepareStatement("SELECT * FROM Order WHERE orderId = ?")) {
            findQuery.setInt(1, orderId);
            ResultSet rs = findQuery.executeQuery();

            if (!rs.next()) {
                throw new Order.OrderNotFoundException("Failed to find order with orderId ["+orderId+"]");
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to find order with orderId ["+orderId+"]", e);
            throw e;
        }

        try (PreparedStatement orderQuery = db.prepareStatement("SELECT * FROM Order WHERE orderId = ?");
             PreparedStatement orderLineQuery = db.prepareStatement("SELECT * FROM OrderLine WHERE orderId = ?")
        ) {
            orderQuery.setInt(1, orderId);
            orderLineQuery.setInt(1, orderId);

            ResultSet rsOrder = orderQuery.executeQuery();
            ResultSet rsOrderLine = orderLineQuery.executeQuery();

            List<OrderLine> items = new ArrayList<OrderLine>();
            while (rsOrderLine.next()) {
                items.add(new OrderLine(
                        rsOrderLine.getInt("orderId"),
                        rsOrderLine.getString("productCode"),
                        rsOrderLine.getInt("quantity")
                ));
            }

            Order order = new Order(
                    rsOrder.getInt("orderId"),
                    rsOrder.getInt("personId"),
                    rsOrder.getDate("date"),
                    Order.OrderStatus.valueOf(rsOrder.getString(4)),
                    items
            );

            return order;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to fetch orders with orderId ["+orderId+"]", e);
            throw e;
        }
    }

    /**
     * Returns all orders with a given status
     * @param status Either PENDING, CONFIRMED, FULFILLED
     * @return List of orders
     * @throws SQLException
     */
    public static List<Order> GetOrdersWithStatus(Order.OrderStatus status) throws SQLException {
        try (PreparedStatement orderQuery = db.prepareStatement("SELECT * FROM Order WHERE status = ?")) {
            orderQuery.setString(1, status.toString());

            ResultSet rs = orderQuery.executeQuery();
            if (!rs.next()) {
                return null;
            }

            List<Order> orders = new ArrayList<Order>();
            while (rs.next()) {
                orders.add(new Order(
                            rs.getInt("orderId"),
                            rs.getInt("personId"),
                            rs.getDate("date"),
                            Order.OrderStatus.valueOf(rs.getString(4))
                ));
            }

            return orders;
        }catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to fetch orders with status ["+status+"]", e);
            throw e;
        }
    }

    public static boolean CreateOrder(Order order) throws SQLException {
        db.setAutoCommit(false);
        int id = -1;

        try (PreparedStatement s = db.prepareStatement("INSERT INTO Order VALUES (default,?,?,?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement r = db.prepareStatement("INSERT INTO OrderLine VALUES (?,?,?)");
        ) {

            Object[] fields = order.GetFields().toArray();

            s.setInt(1, (Integer) fields[0]); // personId
            s.setString(2, (String) fields[1]); // date
            s.setString(3, fields[2].toString()); // status
            s.executeUpdate();

            ResultSet rs = s.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
                order.setOrderId(id);
            } else {
                throw new InternalError("Failed to insert into Order table");
            }

            order.getItemsList().forEach((item) -> {
                Object[] olFields = item.GetFields().toArray();

                try {
                    r.setInt(1, (Integer) olFields[0]);
                    r.setString(2, olFields[1].toString());
                    r.setInt(3, (Integer) olFields[2]);
                    r.executeUpdate();
                } catch (SQLException e) {
                    DatabaseBridge.databaseError("Failed to insert new order line", e);
                }

            });

            db.commit();
        } catch (SQLException | InternalError e) {
            DatabaseBridge.databaseError("Failed to insert new order", e);
            db.rollback();
            throw e;
        } finally {
            db.setAutoCommit(true);
        }

        return true;
    }

    public static BankDetail CreatePaymentInfo(String cardNumber, Date expiryDate, String securityCode)
            throws SQLException, BankDetail.InvalidBankDetailsException {
        int id = -1;
        boolean isCardValid = LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(cardNumber);
        if (!isCardValid) {
            throw new BankDetail.InvalidBankDetailsException("Card number invalid, failed Luhn check ["+cardNumber+"]");
        }
        // Expiry date should be in the format MM/YY
        if (expiryDate.before(new java.util.Date())) {
            throw new BankDetail.InvalidBankDetailsException("Card is expired ["+expiryDate.toLocalDate()+"]");
        }

        if (securityCode.length() != 3) {
            throw new BankDetail.InvalidBankDetailsException("Security code was an invalid length ["+securityCode+"]");
        }

        String cardName = "Card ending in " + cardNumber.substring(cardNumber.length() - 4);
        try (PreparedStatement cardQuery = db.prepareStatement("INSERT INTO CardDetails VALUES (?, ?, ?, ?")) {
            byte[] encryptionKey = AppContext.getEncryptionKey();
            String encryptedCardNumber = Crypto.encryptString(cardNumber, encryptionKey);
            String encryptedSecurityCode = Crypto.encryptString(securityCode, encryptionKey);

            cardQuery.setString(1, cardName);
            cardQuery.setString(2, encryptedCardNumber);
            cardQuery.setDate(3, expiryDate);
            cardQuery.setString(4, encryptedSecurityCode);

            cardQuery.executeUpdate();
            ResultSet rs = cardQuery.getGeneratedKeys();

            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                throw new InternalError("Failed to insert into BankDetails table");
            }

            return new BankDetail(id, cardName, cardNumber, expiryDate, securityCode);
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to insert new payment info ["+cardName+"]", e);
            throw e;
        } catch (InvalidKeyException e) {
            Crypto.cryptoError("Error whilst encrypting card number, encryption key was invalid", e);
            throw new RuntimeException(e);
        }
    }

    public static BankDetail GetBankDetailsById(int id) throws SQLException, InvalidKeyException {
        try(PreparedStatement bankQuery = db.prepareStatement("SELECT * FROM BankDetails WHERE paymentId = ?")) {
            bankQuery.setInt(1, id);
            ResultSet rs = bankQuery.executeQuery();

            if (!rs.next()) {
                throw new BankDetail.BankAccountNotFoundException("Failed to find bank details with id ["+id+"]");
            }

            byte[] encryptionKey = AppContext.getEncryptionKey();
            String decryptedCardNumber = Crypto.decryptString(rs.getString("cardNumber"), encryptionKey);
            String decryptedSecurityCode = Crypto.decryptString(rs.getString("securityCode"), encryptionKey);

            return new BankDetail(
                    rs.getInt("paymentId"),
                    rs.getString("cardName"),
                    decryptedCardNumber,
                    rs.getDate("expiryDate"),
                    decryptedSecurityCode
            );
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to fetch bank details with id ["+id+"]", e);
            throw e;
        } catch (InvalidKeyException e) {
            // Thrown when the user tries to decrypt another user's card with an invalid encryption key
            // I.e. they are trying to decrypt a card that isn't theirs
            Crypto.cryptoError("User tried to decrypt card with id ["+id+"] but they used the wrong encryption key", e);
            throw e;
        }
    }
}
