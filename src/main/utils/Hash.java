package utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Static utility class for abstracting hash algorithm implementation
 */
public class Hash {
    public static String hashString(String input) {
        return BCrypt.withDefaults().hashToString(12, input.toCharArray());
    }

    public static boolean verifyString(String input, String hashString) {
        return BCrypt.verifyer().verify(input.toCharArray(), hashString).verified;
    }
}
