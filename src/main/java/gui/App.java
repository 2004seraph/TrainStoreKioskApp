package gui;

import controllers.AppContext;
import entity.StoreAttributes;
import gui.person.*;
import gui.staff.ManagerScreen;
import gui.staff.StockManagementScreen;

import javax.swing.*;

public class App {
    private final TabbedGUIContainer screenController;

    public App() { // THIS IS RAN ONCE
        screenController = new TabbedGUIContainer(0.2f);

        JFrame frame = AppContext.getWindow();
        frame.getContentPane().add(screenController);

        loginState();

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    public void loginState() {
        screenController.removeAllTabs();

        JPanel registerPage = new Register(this);
        JPanel loginPage = new Login(this);
        screenController.insertTab("Register", registerPage);
        screenController.insertTab("Login", loginPage);

        screenController.switchTab("Login");
    }

    /**
     * The logged-in screen with each dashboard this role has access too
     */
    public void userState(StoreAttributes.Role userRole) {
        screenController.removeAllTabs();
        switch (userRole) {
            case MANAGER:
                ManagerScreen manScreen = new ManagerScreen();
                screenController.insertDivider();
                screenController.insertTab("User Management", manScreen);
                // deliberate fallthrough
            case STAFF:
                StockManagementScreen sms = new StockManagementScreen();
                screenController.insertTab("Stock Management", sms);
                screenController.insertDivider();
                break;
        }


        JPanel shopPage = new Shop();
        JPanel ordersPage = new PastOrders();
        JPanel profilePage = new Profile();
        JPanel cartPage = new Cart();

//        screenController.insertTab("Logout", logoutPage);
        screenController.insertNonTabButton(new JButton("Logout"));

        screenController.insertTab("Cart", cartPage);
        screenController.insertTab("Profile", profilePage);
        screenController.insertTab("My Orders", ordersPage);
        screenController.insertTab("Shop", shopPage);

        screenController.switchTab("Profile");
    }
}
