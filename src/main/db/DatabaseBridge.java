package db;

import java.sql.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Maintainers: Samuel Taseff
 *
 */

/**
 * This is a singleton class that manages the single db connection and also dispatches prepared statements to operations.
 */
public final class DatabaseBridge {
    // Nested types
    private static final class NoDatabaseConnectorException extends RuntimeException {
        public NoDatabaseConnectorException(String errorMessage) {
            super(errorMessage);
        }
    }

    // static members
    private static final String DB_URL = "jdbc:mysql://stusql.dcs.shef.ac.uk:3306/team005";

    // ensure you only use URL options specific to the MySQL connector
    // https://dev.mysql.com/doc/connectors/en/connector-j-reference-configuration-properties.html
    private static final List<String> DB_OPTS = Arrays.asList(
            "connectTimeout=90"     // Measured in milliseconds
    );
    private static final String DB_USERNAME = "team005";
    private static final String DB_PASSWORD = "Uajee5tha";

    private static DatabaseBridge Instance;

    // Instance members
    private Connection connection = null;

    // Static methods
    public static void databaseLog(String... msg) {
        System.out.print("[db.DatabaseBridge] ");
        for (String i : msg) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
    }

    public static void databaseError(String extraContext, Throwable e) {
        databaseLog(
                "ERROR:",
                extraContext + "\n\t",
                e.getClass().getCanonicalName() + ":",
                e.getLocalizedMessage()
        );
        e.printStackTrace();
    }

    public static Boolean isDriverLoaded()  {
        Enumeration<Driver> list = DriverManager.getDrivers();
        while (list.hasMoreElements()) {
            Driver driver = list.nextElement();
            if (driver.toString().contains("mysql")) {
                return true;
            }
        }

        return false;
    }

    public static DatabaseBridge Instance() {
        if (Instance == null) {
            Instance = new DatabaseBridge();
        }

        return Instance;
    }

    // Instance methods
    private DatabaseBridge() {
        databaseLog("Initialized");
        if (isDriverLoaded()) {
            databaseLog("Found MySQL DB driver");
        } else {
            databaseError(
                    "",
                    new NoDatabaseConnectorException("MySQL DB driver not found")
            );
        }
    }

    /**
     * Opens a connection to the database, will time-out after a certain number of milliseconds
     * @throws SQLException
     */
    public void openConnection() throws SQLException {
        StringBuilder connectionOpts = new StringBuilder("?");
        for (String opt : DB_OPTS) {
            connectionOpts.append(opt);
            connectionOpts.append("&");
        }
        try {
            connection = DriverManager.getConnection(
                    DB_URL + connectionOpts.toString(),
                    DB_USERNAME,
                    DB_PASSWORD
            );
            databaseLog("Opened connection");
        } catch (com.mysql.cj.jdbc.exceptions.CommunicationsException e) {
            databaseError("Failed to open connection, you may not be connected to the VPN", e);
        } catch (SQLException e) {
            databaseError("Failed to open connection", e);
            throw e;
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                databaseLog("Connection closed successfully");
            } catch (SQLException e) {
                databaseError("Failed to close connection gracefully", e);
            } finally {
                connection = null;
            }
        }
    }

    /**
     * How you begin any query on this database, this returns a prepared statement with your SQL that you can then set the parameters and execute
     * @param sql
     * @return Your statement ready for you to add the paramaters and execute it
     * @throws SQLException
     */
    public PreparedStatement BeginQuery(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }
}
