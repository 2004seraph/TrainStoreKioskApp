package utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Static utility class for abstracting hash algorithm implementation
 */
public class Crypto {
    public static String hashString(String input) {
        return BCrypt.withDefaults().hashToString(12, input.toCharArray());
    }

    public static boolean verifyString(String input, String hashString) {
        char[] pwd = input.toCharArray();
        return BCrypt.verifyer().verify(pwd, hashString).verified;
    }

    public static byte[] deriveEncryptionKey(String password) {
        final int iterations = 1000;
        final int keyLength = 32;
        byte[] derrivedKey;
        byte[] salt = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0xA};

        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such algorithm");
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
