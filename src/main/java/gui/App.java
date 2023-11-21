package gui;

import controllers.AppContext;

import javax.swing.*;

public class App {
    private static JFrame frame;
    private static TabbedGUIContainer screenController = new TabbedGUIContainer(0.2f);

    public static void loggedOutScreen() {
        frame = AppContext.getWindow();
        JPanel registerPage = new Register();
        JPanel loginPage = new Login();

        screenController.removeAllTabs();
        screenController.insertTab("Register", registerPage, new TabbedGUIContainer.ScreenRequirement() {
            @Override
            public boolean canOpen() {
                return true;
            }
        });
        screenController.insertTab("Login", loginPage, new TabbedGUIContainer.ScreenRequirement() {
            @Override
            public boolean canOpen() {
                return true;
            }
        });

        screenController.switchTab("Login");


        frame.getContentPane().add(screenController);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    public static void loggedInScreen() {
        screenController.removeAllTabs();

        JPanel profile = new Profile();

        screenController.insertTab("Profile", profile, new TabbedGUIContainer.ScreenRequirement() {
            @Override
            public boolean canOpen() {
                return true;
            }
        });

        screenController.switchTab("Profile");
    }
}
