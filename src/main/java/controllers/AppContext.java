package controllers;

import entity.user.Person;

import javax.swing.*;

import javax.swing.plaf.metal.MetalLookAndFeel;
public class AppContext {
    enum AppStyle {
        METAL("javax.swing.plaf.metal.MetalLookAndFeel"),
        NIMBUS("javax.swing.plaf.nimbus.NimbusLookAndFeel"),
        GTK("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

        private final String lookAndFeel;

        AppStyle(String lookAndFeel) {
            this.lookAndFeel = lookAndFeel;
        }

        @Override
        public String toString() {
            return lookAndFeel;
        }
    }

    private static JFrame window;
    public static JFrame getWindow() {
        if (window == null) {
            try {
                UIManager.setLookAndFeel(AppStyle.NIMBUS.toString());
            } catch (Exception e) {
                System.out.println("Failed to style app, continuing with default style");
            }
            window = new JFrame("Trains of Sheffield");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        return window;
    }

    private static Person currentUser;

    public static boolean queueStoreReload;

    private static byte[] encryptionKey;
    public static byte[] getEncryptionKey() {
        return encryptionKey;
    }

    public static void setEncryptionKey(byte[] key) {
        encryptionKey = key;
    }

    public static Person getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Person user) {
        currentUser = user;
    }

    /**
     * Clear all app state
     */
    public static void logOut() {
        encryptionKey = null;
        currentUser = null;
    }
}
