package gui;

import controllers.AppContext;

import javax.swing.*;
import java.awt.*;

public class App {
    private final TabbedGUIContainer screenController;

    public App() { // THIS IS RAN ONCE
        screenController = new TabbedGUIContainer(0.2f);

        JFrame frame = AppContext.getWindow();
        frame.getContentPane().add(screenController);
//        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        loginState();
        loginState();

        frame.setVisible(true);
    }

    public void loginState() {
        screenController.removeAllTabs();

        JPanel registerPage = new Register(this);
        JPanel loginPage = new Login(this);
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
    }

    /**
     * The logged-in screen with each dashboard this role has access too
     */
    public void userState() {
        screenController.removeAllTabs();

        JPanel profile = new Profile(this);

//        JPanel blackWindow = new JPanel();
//        blackWindow.setBackground(Color.BLACK);
//        JPanel blueWindow = new JPanel();
//        blueWindow.setBackground(Color.CYAN);

        screenController.insertTab("Profile", profile, new TabbedGUIContainer.ScreenRequirement() {
            @Override
            public boolean canOpen() {
                return true;
            }
        });
//        screenController.insertTab("black", blackWindow, new TabbedGUIContainer.ScreenRequirement() {
//            @Override
//            public boolean canOpen() {
//                return true;
//            }
//        });
//        screenController.insertTab("blue", blueWindow, new TabbedGUIContainer.ScreenRequirement() {
//            @Override
//            public boolean canOpen() {
//                return true;
//            }
//        });

        screenController.switchTab("Profile");
    }
}
