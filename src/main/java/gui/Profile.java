package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Profile extends JPanel{
    private JLabel myProfileLabel;
    private JButton button1;
    private JButton button2;

    private App app;

    public Profile(App app) {
        myProfileLabel = new JLabel("My Profile");
        button1 = new JButton("Button 1");
        button2 = new JButton("Logout");

        this.add(myProfileLabel);
        this.add(button1);
        this.add(button2);

        this.app = app;

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.loginState();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Profile");
        App app = new App();
        frame.setContentPane(new Profile(app));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
