package gui.person;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

import controllers.AppContext;
import db.*;
import entity.user.*;
import entity.*;
import gui.App;
import gui.components.TabbedGUIContainer;
import utils.*;

public class Register extends JPanel implements TabbedGUIContainer.TabPanel {
    // only actually storing the fields we are going to use later
    private final JTextField forename;
    private final JTextField surname;
    private final JTextField email;
    private final JTextField password;
    private final JTextField passwordConfirmation;
    private final JTextField houseNumber;
    private final JTextField streetName;
    private final JTextField cityName;
    private final JTextField postCode;

    private TabbedGUIContainer parent;

    private static final int inset = 120;
    private App app;

    public Register(App app) {
        this.app = app;

        setBorder(new EmptyBorder(inset, inset, inset, inset));

        //construct components
        JLabel registerLabel = new JLabel("<html><h1>Register a new account</h1></html>");
        forename = new JTextField();
        JLabel forenameLabel = new JLabel("Forename");
        JLabel surnameLabel = new JLabel("Surname");
        surname = new JTextField(5);
        JLabel emailLabel = new JLabel("Email");
        email = new JTextField (5);
        JLabel passwordLabel = new JLabel("Password");
        password = new JPasswordField (5);
        JLabel passwordConfirmationLabel = new JLabel("Password Confirmation");
        passwordConfirmation = new JPasswordField (5);

        JLabel addressLabel = new JLabel("<html><h2>Address</h2></html>");
        JLabel houseNumberLabel = new JLabel("House Number");
        houseNumber = new JTextField(5);
        JLabel streetNameLabel = new JLabel("Street Name");
        streetName = new JTextField(5);
        JLabel cityNameLabel = new JLabel("City Name");
        cityName = new JTextField(5);
        JLabel postCodeLabel = new JLabel("PostCode");
        postCode = new JTextField(5);

        JButton registerButton = new JButton("Create Account");
        JLabel loginLabel = new JLabel("<html><u><font color='blue'>Login</font></u></html>");

        // using an actual layout manager instead of setting everything manually
        setLayout (new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0.001;
        gbc.gridx = 0;
        gbc.gridy = 0;

        //add components
        gbc.gridwidth = 2;
        add(registerLabel, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        add(forenameLabel, gbc);
        gbc.gridx++;
        add(forename, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        add(surnameLabel, gbc);
        gbc.gridx++;
        add(surname, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
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
        add(passwordConfirmationLabel, gbc);
        gbc.gridx++;
        add(passwordConfirmation, gbc);
        gbc.gridy++;

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        add(addressLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        add(houseNumberLabel, gbc);
        gbc.gridx++;
        add(houseNumber, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        add(streetNameLabel, gbc);
        gbc.gridx++;
        add(streetName, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        add(cityNameLabel, gbc);
        gbc.gridx++;
        add(cityName, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        add(postCodeLabel, gbc);
        gbc.gridx++;
        add(postCode, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        loginLabel.setBorder(new EmptyBorder(0, 7, 0, 0));
        add (loginLabel, gbc);
        gbc.gridx++;
        add(registerButton, gbc);

        // Adding a blank jpanel to make the layout nicer
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weighty = 1;
        add(new JPanel(), gbc);

        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.switchTab("Login");
            }
        });


        // When clicking registerButton validate the input, then close register window and open login window
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                if (validateCompulsoryInput() && validateFormat()) {
                    // Open connection with the database
                    DatabaseBridge db = DatabaseBridge.instance();
                    try{
                        db.openConnection();
                        // Create a new address
                        Address newAddress = new Address(
                            houseNumber.getText(),
                            streetName.getText(),
                            cityName.getText(),
                            postCode.getText()
                        );
                        Address.CreateAddress(newAddress);

                        // Create new person
                        String passwordHash = Crypto.hashString(password.getText());
                        Person newPerson = new Person(
                            forename.getText(),
                            surname.getText(),
                            email.getText(),
                            passwordHash,
                            houseNumber.getText(),
                            postCode.getText()
                        );

                        Person.createPerson(newPerson);

                        AppContext.setEncryptionKey(Crypto.deriveEncryptionKey(password.getText()));
                        AppContext.setCurrentUser(newPerson);
                        app.userState(newPerson.getRole());

                        JOptionPane.showMessageDialog(AppContext.getWindow(), "Registration successful");
                    } catch (SQLException sqlError) {
                        JOptionPane.showMessageDialog(AppContext.getWindow(), sqlError.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        db.closeConnection();
                    }
                }
            }
    
        });
    }

    private boolean validateCompulsoryInput() {
        if (forename.getText().isEmpty()) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Forename cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (surname.getText().isEmpty()) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Surname cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (email.getText().isEmpty()) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Email cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (password.getText().isEmpty()) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Password cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (passwordConfirmation.getText().isEmpty()) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Password confirmation cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!password.getText().equals(passwordConfirmation.getText())) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Passwords do not match", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (houseNumber.getText().isEmpty()) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "House number cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (streetName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Street name cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (cityName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "City name cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (postCode.getText().isEmpty()) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Postcode cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            DatabaseBridge db = DatabaseBridge.instance();
            db.openConnection();
            if (Person.getPersonByEmail(email.getText()) != null) {
                JOptionPane.showMessageDialog(AppContext.getWindow(), "Email already exists", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            db.closeConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Account creation error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }

    private boolean validateFormat(){
        // regular expressions for email, postcode and password

        // Validate the fields with the regular expressions
        if (!Person.validateEmail(email.getText())) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Invalid email", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!Address.validatePostcode(postCode.getText())) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Invalid postcode", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!Person.validatePassword(password.getText())) {
            JOptionPane.showMessageDialog(
                    AppContext.getWindow(),
                    "Password must be at least 8 characters long and contain at least one number, " +
                            "one uppercase letter and one lowercase letter", "Error", JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {
        this.parent = cont;
    }

    @Override
    public void onSelected() {

    }
}
