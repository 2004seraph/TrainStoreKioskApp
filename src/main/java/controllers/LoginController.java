package controllers;

import db.DatabaseBridge;
import db.DatabaseOperation;
import entity.user.Person;
import utils.Hash;

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
            Person user = DatabaseOperation.GetPersonByEmail(email);
            if (user == null) {
                return null;
            }

            boolean pwdMatch = Hash.verifyString(password, user.getPassword());

            if (pwdMatch) {
                return user;
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
