package gui.person;

import controllers.AppContext;
import controllers.LoginController;
import controllers.OrderController;
import entity.user.Person;
import gui.App;
import gui.components.TabbedGUIContainer;
import utils.Crypto;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Login extends JPanel implements TabbedGUIContainer.TabPanel {
    private final JTextField email;
    private final JPasswordField password;

    private TabbedGUIContainer parent;

    private App app;

    private static final int inset = 120;

    public Login(App ctx) {
        app = ctx;

        // adds inner padding
        setBorder(new EmptyBorder(inset, inset, inset, inset));

        //construct components
        JLabel loginLabel = new JLabel("<html><h1>Login</h1></html>");

        JLabel emailLabel = new JLabel("Email");
        email = new JTextField (5);
//        email.setText("staff@sheffield.ac.uk");
//        email.setText("sam@sheffield.ac.uk");

        JLabel passwordLabel = new JLabel("Password");
        password = new JPasswordField (5);
//        password.setText("Staff12345!");
//        password.setText("password123");

        JButton loginButton = new JButton("Login");
        JLabel registerLabel = new JLabel("<html><u><font color='blue'>Register Now</font></u></html>");

        // please do not ever set a preferred height or width for screens, it ruins the app's responsive design

        // using an actual layout manager instead of setting everything manually
        setLayout (new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0.001;
        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.gridwidth = 2;
        add(loginLabel, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        //add components
        add(emailLabel, gbc);
        gbc.gridx++;
        add(email, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        add(passwordLabel, gbc);
        gbc.gridx++;
        add(password, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        registerLabel.setBorder(new EmptyBorder(0, 7, 0, 0));
        add(registerLabel, gbc);
        gbc.gridx++;
        add(loginButton, gbc);

        // Adding a blank jpanel to make the layout nicer
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weighty = 1;
        add(new JPanel(), gbc);

        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.switchTab("Register");
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String emailInput = email.getText();
                char[] passwordInput = password.getPassword();

                if (emailInput == null || passwordInput == null || emailInput.isEmpty() || passwordInput.length == 0) {
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Please enter a valid email and password", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String pwd = String.valueOf(passwordInput);
                AppContext.setEncryptionKey(Crypto.deriveEncryptionKey(pwd));

                Person user = LoginController.authenticateUser(emailInput, pwd);
                if (user != null) {
                    System.out.println("Successfully authenticated user");
                    AppContext.setCurrentUser(user);
                    OrderController.newOrder();
                    app.userState(user.getRole());
                } else {
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Incorrect email or password", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {
        parent = cont;
    }

    @Override
    public void onSelected() {

    }
}
