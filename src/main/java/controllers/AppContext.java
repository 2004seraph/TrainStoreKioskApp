package controllers;

import javax.swing.*;

public class AppContext {
    enum AppStyle {
//        GTK = ""
    }

    private static JFrame window;
    public static JFrame getWindow() {
        if (window == null) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } catch (Exception e) {
                System.out.println("Failed to style app, continuing with default style");
            }
            window = new JFrame("Store Kiosk");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        return window;
    }

//    Person currentUser

    private static byte[] encryptionKey;
    public static byte[] getEncryptionKey() {
        return encryptionKey;
    }

    public static void setEncryptionKey(byte[] key) {
        encryptionKey = key;
    }
}
