package db;

import entity.*;
import entity.user.*;
import entity.product.*;

import java.sql.SQLException;

import static entity.Attributes.*;

public final class DatabaseOperations {

    private static DatabaseBridge connection = null;

    /**
     * This class should not be instantiated
     */
    private DatabaseOperations() { }

    /**
     * Sets the database connection on which these actions affect
     * @param conn A database connection bridge
     */
    public static void SetConnection(DatabaseBridge conn) {
        connection = conn;
    }

    public static Person GetPersonByLoginCredentials(
            String email,
            String password
    ) throws SQLException {
        return new Person();
    }

    public static ROLE GetRoleByPersonID(
            String personID
    ) throws SQLException {
        return ROLE.USER;
    }
}
