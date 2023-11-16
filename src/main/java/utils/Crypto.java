package utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
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
public class Crypto {
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
            System.out.println("No such algorithm");
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static String encryptString(String input, byte[] key) throws
            InvalidAlgorithmParameterException, InvalidKeyException,
            UnsupportedEncodingException, NoSuchPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

        byte[] cipherText = cipher.doFinal(input.getBytes("UTF-8"));
        byte[] encryptedData = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encryptedData, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decryptString(String input, byte[] key) throws
            InvalidAlgorithmParameterException, InvalidKeyException,
            UnsupportedEncodingException, NoSuchPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
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
        return new String(decryptedText, "UTF-8");
    }

    public static void main(String[] args) {
        byte[] key = deriveEncryptionKey("password123");
        System.out.println("Key: " + Base64.getEncoder().encodeToString(key));

        try {
            String encrypted = encryptString("Hello World", key);
            System.out.println("Encrypted: " + encrypted);

            String decrypted = decryptString(encrypted, key);
            System.out.println("Decrypted: " + decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
