package db;

import entity.user.*;
import entity.*;
import entity.StoreAttributes.Role;

import java.sql.*;

import static entity.StoreAttributes.*;

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

    // Resultset of products for store view

    // update a single products stock level

    // change an order status

    // a person can update their personal info

    // promoting/demoting/changing a user's role

    // getting a list of orders with a certain status
}
