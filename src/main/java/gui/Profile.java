package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Profile extends JPanel{
    private JLabel myProfileLabel;
    private JButton button1;
    private JButton button2;

    public Profile() {
        myProfileLabel = new JLabel("My Profile");
        button1 = new JButton("Button 1");
        button2 = new JButton("Logout");

        this.add(myProfileLabel);
        this.add(button1);
        this.add(button2);

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.loggedOutScreen();
            }
        });
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Profile");
        frame.setContentPane(new Profile());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
