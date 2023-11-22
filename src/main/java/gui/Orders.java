package gui;

import controllers.AppContext;

import javax.swing.*;

public class Orders extends JPanel{

        private static class Order extends JPanel{

        }
        public Orders() {
        }

        public static void main(String[] args) {
            JFrame frame = AppContext.getWindow();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setContentPane(new Orders());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
}
