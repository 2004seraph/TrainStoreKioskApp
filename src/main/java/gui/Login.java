package gui;

import controllers.AppContext;
import controllers.LoginController;
import entity.user.Person;
import utils.Crypto;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;

public class Login extends JPanel implements TabbedGUIContainer.TabPanel {
    private JButton loginButton;
    private JLabel passwordLabel;
    private JTextField email;
    private JLabel emailLabel;
    private JLabel loginLabel;
    private JLabel notAUserLabel;
    private JLabel reigsterLabel;
    private JPasswordField password;

    private TabbedGUIContainer parent;

    public Login() {
        //construct components
        loginButton = new JButton ("Login");
        passwordLabel = new JLabel ("Password:");
        email = new JTextField (5);
        emailLabel = new JLabel ("Email:");
        loginLabel = new JLabel ("<html><h1>LOGIN</h1></html>");
        notAUserLabel = new JLabel ("Not a user? ");
        reigsterLabel = new JLabel ("<html><u><font color='blue'>Register Now</font></u></html>");
        password = new JPasswordField (5);

        //adjust size and set layout
//        setPreferredSize (new Dimension (483, 425));
        setLayout (null);

        //add components
        add (loginButton);
        add (passwordLabel);
        add (email);
        add (emailLabel);
        add (loginLabel);
        add (notAUserLabel);
        add (reigsterLabel);
        add (password);

        //set component bounds (only needed by Absolute Positioning)
        loginButton.setBounds (140, 215, 100, 25);
        passwordLabel.setBounds (140, 155, 100, 25);
        email.setBounds (140, 130, 200, 25);
        emailLabel.setBounds (140, 105, 100, 25);
        loginLabel.setBounds (210, 65, 100, 35);
        notAUserLabel.setBounds (140, 245, 70, 20);
        reigsterLabel.setBounds (210, 245, 100, 20);
        password.setBounds (140, 180, 200, 25);

        // When clicking reigsterLabel, close this window and open Register window

        // this needs to be made to work with tab screen system

//        reigsterLabel.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                Window w = SwingUtilities.getWindowAncestor(Login.this);
//                w.dispose();
//                Register.startRegister();
//            }
//        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String emailInput = email.getText();
                char[] passwordInput = password.getPassword();

                if (emailInput == null || passwordInput == null) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid email and password");
                    return;
                }

                if (emailInput.isEmpty() || passwordInput.length == 0) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid email and password");
                    return;
                }

                String pwd = String.valueOf(passwordInput);

                Person user = LoginController.authenticateUser(emailInput, pwd);
                if (user != null) {
                    System.out.println("Successfully authenticated user");
                    AppContext.setEncryptionKey(Crypto.deriveEncryptionKey(pwd));
                    AppContext.setCurrentUser(user);
                    Dashboard.generateDashboard();
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect email or password");
                    System.out.println("Authentication was unsuccessful");
                }
            }
        });
    }

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {
        parent = cont;
    }
}
