package db;

import entity.user.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        public PreparedStatement BeginQuery(String sql) throws SQLException {
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
            s.setInt(7, (Integer) fields[6]); // paymentid
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
            DatabaseBridge.databaseError("Failed to insert new user");
            db.rollback();
            throw e;
        }

        db.setAutoCommit(true);

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

        PreparedStatement personQuery = db.prepareStatement("SELECT * FROM Person WHERE email=?");
        PreparedStatement roleQuery = db.prepareStatement("SELECT * FROM Role WHERE personId=?");
        personQuery.setString(1, email);
        ResultSet res = personQuery.executeQuery();

        Person person;
        if (res.next()) {
            int id = res.getInt(1);
            roleQuery.setInt(1, id);
            ResultSet roles = roleQuery.executeQuery();

            Role userRole = Role.USER;
            // get the highest privildge role this user has and use that
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
    }
}
