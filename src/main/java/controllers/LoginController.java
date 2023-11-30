package controllers;

import db.DatabaseBridge;
import entity.user.Person;
import utils.Crypto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class LoginController {
    private static void log(String... msg) {
        System.out.print("[LoginController] ");
        for (String i : msg) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
    }

    public static void logError(String extraContext, Throwable e) {
        log(
                "ERROR ->",
                extraContext + "\n\t",
                e.getClass().getCanonicalName() + ":",
                e.getLocalizedMessage()
        );
        e.printStackTrace();
    }

    /**
     * Authenticates user and fetches the user's profile
     * @param email email input
     * @param password password input
     * @return Person instance of the user if password is correct, otherwise return null
     */
    public static Person authenticateUser(String email, String password) {
        DatabaseBridge db = DatabaseBridge.instance();
        try {
            db.openConnection();
            PreparedStatement q = db.prepareStatement("SELECT password FROM Person WHERE email = ?");
            q.setString(1, email);

            ResultSet rs = q.executeQuery();
            if (!rs.next()) {
                return null;
            }

            boolean pwdMatch = Crypto.verifyString(password, rs.getString("password"));

            if (pwdMatch) {
                return Person.getPersonByEmail(email);
            }
        } catch (SQLException e) {
            logError("Failed to fetch user", e);
            return null;
        } finally {
            db.closeConnection();
        }

        return null;
    }
}
