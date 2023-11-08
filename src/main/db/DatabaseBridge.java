package db;

import java.sql.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Maintainers: Sam Taseff
 * <p>
 * This is a singleton class (meaning you cannot instantiate it, you may only get a reference using the Instance() method)
 * that manages the single db connection and also dispatches prepared statements to operations
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
    /**
     * Logs a nicely formatted message concerning the db to the console
     * @param msg Any number of string parameters to be output on this line
     */
    public static void databaseLog(String... msg) {
        System.out.print("[DatabaseBridge] ");
        for (String i : msg) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
    }

    /**
     * Logs a nicely formatted error message concerning the db to the console
     * @param extraContext This is information specific to the place the error happened, a description of what the error would mean
     * @param e This is an exception, it can be one that was caught or one you quickly instantiate yourself
     */
    public static void databaseError(String extraContext, Throwable e) {
        databaseLog(
                "ERROR:",
                extraContext + "\n\t",
                e.getClass().getCanonicalName() + ":",
                e.getLocalizedMessage()
        );
        e.printStackTrace();
    }

    /**
     * Checks if the MySQL connector is functioning properly and detected by Java
     * @return A bool if it was successful
     */
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

    /**
     * It is correct that this constructor is private, do not instantiate this class, use the Instance() method
     * @return Not for you
     */
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
                    "(Initialization)",
                    new NoDatabaseConnectorException("MySQL DB driver not found")
            );
        }
    }

    /**
     * Opens a connection to the database, will time out after a certain number of milliseconds
     * @throws SQLException Concerning a failed connection attempt to the database
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

        } catch (java.sql.SQLRecoverableException e) { // The superclass of the MySQL connector's communication exception
            databaseError("Failed to open connection, you may not be connected to the VPN", e);
        } catch (SQLException e) {
            databaseError("Failed to open connection", e);
            throw e;
        }
    }

    /**
     * Gracefully closes the database connection, to use this class again, you must call openConnection() once more
     */
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
     * @param sql The SQL you wish to execute on the database
     * @return Your statement ready for you to add the parameters and execute it
     * @throws SQLException Concerning something to do with your query or update
     */
    public PreparedStatement BeginQuery(String sql) throws SQLException {
        try {
            return connection.prepareStatement(sql);
        } catch (NullPointerException e) {
            databaseError("A connection has not been opened yet, please call `openConnection()` before this method", e);
            throw e;
        }
    }
}
