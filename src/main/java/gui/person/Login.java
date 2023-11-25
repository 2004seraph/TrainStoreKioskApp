package gui.person;

import controllers.AppContext;
import controllers.LoginController;
import controllers.OrderController;
import entity.user.Person;
import gui.App;
import gui.TabbedGUIContainer;
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
        email.setText("najaaznabhan@gmail.com");

        JLabel passwordLabel = new JLabel("Password");
        password = new JPasswordField (5);
        password.setText("Naajid12345!");

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

        //set component bounds (only needed by Absolute Positioning) - Please never do this again
//        loginButton.setBounds (140, 215, 100, 25);
//        passwordLabel.setBounds (140, 155, 100, 25);
//        email.setBounds (140, 130, 200, 25); - Please never do this again
//        emailLabel.setBounds (140, 105, 100, 25);
//        loginLabel.setBounds (210, 65, 100, 35);
//        notAUserLabel.setBounds (140, 245, 70, 20); - Please never do this again
//        reigsterLabel.setBounds (210, 245, 100, 20);
//        password.setBounds (140, 180, 200, 25); - Please never do this again

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

                if (emailInput == null || passwordInput == null) {
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Please enter a valid email and password");
                    return;
                }

                if (emailInput.isEmpty() || passwordInput.length == 0) {
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Please enter a valid email and password");
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
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Incorrect email or password");
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
