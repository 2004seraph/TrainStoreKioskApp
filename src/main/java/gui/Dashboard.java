//package gui;
//
//import controllers.AppContext;
//
//import javax.swing.*;
//
//public class Dashboard {
//    private static JFrame frame;
//    private static TabbedGUIContainer dashboard = new TabbedGUIContainer(0.2f);
//
//    public static void generateLoginRegister() {
//        frame = AppContext.getWindow();
//        frame.getContentPane().removeAll();
//        dashboard.removeAllTabs();
//
//        JPanel registerPage = new Register();
//        JPanel loginPage = new Login();
//
//        dashboard.insertTab("Register", registerPage, new TabbedGUIContainer.ScreenRequirement() {
//            @Override
//            public boolean canOpen() {
//                return true;
//            }
//        });
//        dashboard.insertTab("Login", loginPage, new TabbedGUIContainer.ScreenRequirement() {
//            @Override
//            public boolean canOpen() {
//                return true;
//            }
//        });
//
//        dashboard.switchTab("Login");
//
//        frame.getContentPane().add(dashboard);
//        frame.getContentPane().repaint();
//        frame.revalidate();
//        frame.repaint();
//
////        Set the default size of the screen to 500px by 500px
//        frame.setSize(800, 600);
//        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        frame.setVisible(true);
//    }
//
//    public static void generateDashboard () {
//        frame = AppContext.getWindow();
//        frame.getContentPane().removeAll();
//        dashboard.removeAllTabs();
//
//        JPanel profile = new Profile();
//        JPanel logout = new Logout();
//        JPanel cart = new Cart();
//
//        dashboard.insertTab("Profile", profile, new TabbedGUIContainer.ScreenRequirement() {
//            @Override
//            public boolean canOpen() {
//                return true;
//            }
//        });
//        dashboard.insertTab("Logout", logout, new TabbedGUIContainer.ScreenRequirement() {
//            @Override
//            public boolean canOpen() {
//                return true;
//            }
//        });
//
//        dashboard.insertTab("Cart", cart, new TabbedGUIContainer.ScreenRequirement() {
//            @Override
//            public boolean canOpen() {
//                return true;
//            }
//        });
//
//
//        dashboard.switchTab("Profile");
//        frame.getContentPane().add(dashboard);
//        frame.getContentPane().repaint();
//
//        frame.revalidate();
//        frame.repaint();
//    }
//}
