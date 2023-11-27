package utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Static utility class for abstracting hash algorithm implementation
 */
public final class Crypto {
    private Crypto() {}

    public static void cryptoLog(String... msg) {
        System.out.print("[Utils::Crypto] ");
        for (String i : msg) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
    }

    /**
     * Logs a nicely formatted error message concerning the crypto module to the console
     * @param extraContext This is information specific to the place the error happened, a description of what the error would mean
     * @param e This is an exception, it can be one that was caught or one you quickly instantiate yourself
     */
    public static void cryptoError(String extraContext, Throwable e) {
        cryptoLog(
                "ERROR ->",
                extraContext + "\n\t",
                e.getClass().getCanonicalName() + ":",
                e.getLocalizedMessage()
        );
        e.printStackTrace();
    }

    /**
     * If you don't want to print the exception twice
     * @param message An error message giving specific localised context
     */
    public static void cryptoError(String message) {
        cryptoLog(
                "ERROR ->",
                message
        );
    }


    public static String hashString(String input) {
        return BCrypt.withDefaults().hashToString(12, input.toCharArray());
    }

    public static boolean verifyString(String input, String hashString) {
        char[] pwd = input.toCharArray();
        return BCrypt.verifyer().verify(pwd, hashString).verified;
    }

    public static byte[] deriveEncryptionKey(String password) {
        final int ITERATIONS = 65536;
        final int KEY_LENGTH = 256;
        byte[] salt = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0xA};

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKey key = factory.generateSecret(spec);

            return key.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            cryptoError("Algorithm PBKDF2WithHmacSHA256 was not found", e);
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            // Should never be thrown since values are hardcoded
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypts a given input string using AES encryption with CBC mode and PKCS5 padding
     * @param input The string to be encrypted
     * @param key   The encryption key used for AES encryption
     * @return A Base64-encoded string representing the encrypted data
     * @throws InvalidKeyException If the provided encryption key is invalid
     */
    public static String encryptString(String input, byte[] key) throws InvalidKeyException {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

            byte[] cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedData = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(encryptedData);
        } catch(InvalidAlgorithmParameterException e) {
            // This should never be raised because AES is hardcoded
            cryptoError("Algorithm parameter of SecretKeySpec was invalid", e);
            throw new RuntimeException("Algorithm parameter of SecretKeySpec was invalid");
        } catch(InvalidKeyException e) {
            cryptoError("Encryption key was invalid", e);
            throw e;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            // Again, this one should rarely get raised
            cryptoError("AES/CBC/PKCS5Padding is not available on this system", e);
            throw new RuntimeException("AES/CBC/PKCS5Padding is not supported on this system");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            // Should never be raised
            cryptoError("Encryption block size was not padded correctly", e);
            throw new RuntimeException("Encryption block size was not padded correctly");
        }
    }

    public static String decryptString(String input, byte[] key) throws InvalidKeyException{
        try {
            byte[] encryptedData = Base64.getDecoder().decode(input);
            byte[] iv = new byte[16];
            System.arraycopy(encryptedData, 0, iv, 0, iv.length);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);

            byte[] cipherText = new byte[encryptedData.length - 16];
            System.arraycopy(encryptedData, 16, cipherText, 0, cipherText.length);

            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText, StandardCharsets.UTF_8);
        }catch(InvalidAlgorithmParameterException e) {
            // This should never be raised because AES is hardcoded
            cryptoError("Algorithm parameter of SecretKeySpec was invalid", e);
            throw new RuntimeException("Algorithm parameter of SecretKeySpec was invalid");
        } catch(InvalidKeyException e) {
            cryptoError("Encryption key was invalid", e);
            throw e;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            // Again, this one should rarely get raised
            cryptoError("AES/CBC/PKCS5Padding is not available on this system", e);
            throw new RuntimeException("AES/CBC/PKCS5Padding is not supported on this system");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            // Should never be raised
            cryptoError("Encryption block size was not padded correctly", e);
            throw new RuntimeException("Encryption block size was not padded correctly");
        }
    }
}
