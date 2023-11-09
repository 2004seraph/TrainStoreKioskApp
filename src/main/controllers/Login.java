package controllers;

import db.DatabaseOperation;
import entity.user.Person;
import utils.Hash;

import java.sql.SQLException;

/**
 * Singleton class for handling logins
 */
public final class Login {
    private static Login Instance;

    public static Login getInstance() {
        if (Instance == null) {
            Instance = new Login();
        }

        return Instance;
    }

    private void log(String... msg) {
        System.out.print("[LoginController] ");
        for (String i : msg) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
    }

    public void logError(String extraContext, Throwable e) {
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
    public Person authenticateUser(String email, String password) {
        try {
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
        }


        return null;
    }
}
