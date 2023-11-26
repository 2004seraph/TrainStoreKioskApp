package gui;

import controllers.AppContext;
import entity.StoreAttributes;
import gui.components.TabbedGUIContainer;
import gui.person.*;
import gui.staff.ManagerScreen;
import gui.staff.order.OrderManagementScreen;
import gui.staff.stock.StockManagementScreen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        JButton logOutButton = new JButton("Logout");
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppContext.logOut();
                loginState();
            }
        });
        screenController.insertNonTabButton(logOutButton);
        screenController.insertDivider();

        switch (userRole) {
            case MANAGER:
                ManagerScreen manScreen = new ManagerScreen();
                screenController.insertTab("User Management", manScreen);
                screenController.insertDivider();
                // deliberate fallthrough
            case STAFF:
                StockManagementScreen sms = new StockManagementScreen();
                screenController.insertTab("Stock Management", sms);

                OrderManagementScreen oms = new OrderManagementScreen();
                screenController.insertTab("Order Management", oms);
                screenController.insertDivider();
                break;
        }

        JPanel shopPage = new Shop();
        // JPanel pastOrdersPage = new PastOrdersScreen();
        JPanel profilePage = new Profile();
        JPanel cartPage = new Cart();

        // screenController.insertTab("My Orders", pastOrdersPage);
        screenController.insertTab("Profile", profilePage);
        screenController.insertDivider();
        screenController.insertTab("Cart", cartPage);
        screenController.insertTab("Shop", shopPage);

        screenController.switchTab("Profile");
    }
}
