package gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;

import db.*;
import entity.user.*;
import entity.*;
import utils.*;

public class Register extends JPanel{
    private JLabel registerLabel;
    private JTextField forename;
    private JLabel forenameLabel;
    private JLabel surnameLabel;
    private JTextField surname;
    private JLabel emailLabel;
    private JTextField email;
    private JLabel passwordLabel;
    private JTextField password;
    private JLabel passwordConfirmationLabel;
    private JTextField passwordConfirmation;
    private JLabel addressLabel;
    private JLabel houseNumberLabel;
    private JTextField houseNumber;
    private JLabel streetNameLabel;
    private JTextField streetName;
    private JLabel cityNameLabel;
    private JTextField cityName;
    private JLabel postCodeLabel;
    private JTextField postCode;
    private JButton registerButton;
    private JLabel alreadyAUserLabel;
    private JLabel loginLabel;

    private App app;

    public Register(App app) {
        this.app = app;
        //construct components
        registerLabel = new JLabel ("<html><h1>REGISTER</h1></html>");
        forename = new JTextField (5);
        forenameLabel = new JLabel ("Forename:");
        surnameLabel = new JLabel ("Surname:");
        surname = new JTextField(5);
        emailLabel = new JLabel ("Email:");
        email = new JTextField (5);
        passwordLabel = new JLabel ("Password:");
        password = new JPasswordField (5);
        passwordConfirmationLabel = new JLabel ("Password Confirmation:");
        passwordConfirmation = new JPasswordField (5);
        addressLabel = new JLabel ("<html><h2>ADDRESS</h2></html>");
        houseNumberLabel = new JLabel ("House Number:");
        houseNumber = new JTextField (5);
        streetNameLabel = new JLabel ("Street Name:");
        streetName = new JTextField (5);
        cityNameLabel = new JLabel ("City Name:");
        cityName = new JTextField (5);
        postCodeLabel = new JLabel ("PostCode:");
        postCode = new JTextField (5);
        registerButton = new JButton ("Register");
        alreadyAUserLabel = new JLabel ("Already a user?");
        loginLabel = new JLabel ("<html><u><font color='blue'>Login</font></u></html>");

        //adjust size and set layout
//        setPreferredSize (new Dimension (752, 649));
        setLayout (null);

        //add components
        add (registerLabel);
        add (forename);
        add (forenameLabel);
        add (surnameLabel);
        add (surname);
        add (emailLabel);
        add (email);
        add (passwordLabel);
        add (password);
        add (passwordConfirmationLabel);
        add (passwordConfirmation);
        add (addressLabel);
        add (houseNumberLabel);
        add (houseNumber);
        add (streetNameLabel);
        add (streetName);
        add (cityNameLabel);
        add (cityName);
        add (postCodeLabel);
        add (postCode);
        add (registerButton);
        add (alreadyAUserLabel);
        add (loginLabel);

        //set component bounds (only needed by Absolute Positioning)
        registerLabel.setBounds (318, 35, 140, 25);
        forename.setBounds (260, 100, 220, 25);
        forenameLabel.setBounds (260, 80, 100, 25);
        surnameLabel.setBounds (260, 125, 100, 25);
        surname.setBounds (260, 145, 220, 25);
        emailLabel.setBounds (260, 170, 100, 25);
        email.setBounds (260, 190, 220, 25);
        passwordLabel.setBounds (260, 215, 100, 25);
        password.setBounds (260, 235, 220, 25);
        passwordConfirmationLabel.setBounds (260, 260, 145, 25);
        passwordConfirmation.setBounds (260, 280, 220, 25);
        addressLabel.setBounds (326, 319, 140, 25);
        houseNumberLabel.setBounds (260, 360, 100, 25);
        houseNumber.setBounds (260, 380, 220, 25);
        streetNameLabel.setBounds (260, 405, 100, 25);
        streetName.setBounds (260, 425, 220, 25);
        cityNameLabel.setBounds (260, 450, 100, 25);
        cityName.setBounds (260, 470, 220, 25);
        postCodeLabel.setBounds (260, 500, 100, 25);
        postCode.setBounds (260, 520, 220, 25);
        registerButton.setBounds (260, 560, 100, 25);
        alreadyAUserLabel.setBounds (260, 590, 95, 25);
        loginLabel.setBounds (355, 590, 100, 25);


        // When clicking loginLabel, close register window and open login window

        // this needs to be made to work with tab screen system

//        loginLabel.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                Window w = SwingUtilities.getWindowAncestor(Register.this);
//                w.dispose();
//                Login.startLogin();
//            }
//        });

        // When clicking registerButton validate the input, then close register window and open login window
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                if (validateCompulsoryInput() && validateFormat()) {
                    // Open connection with the database
                    DatabaseBridge db = DatabaseBridge.instance();
                    System.out.println("Attempting to connect to database...");
                    try{
                        db.openConnection();
                        System.out.println("Successfully connected to database");
                        // Create a new address
                        Address newAddress = new Address(
                            houseNumber.getText(),
                            streetName.getText(),
                            cityName.getText(),
                            postCode.getText()
                        );
                        DatabaseOperation.CreateAddress(newAddress);

                        // Create new person
                        String passwordHash = Crypto.hashString(password.getText());
                        Person newPerson = new Person(
                            forename.getText(),
                            surname.getText(),
                            email.getText(),
                            passwordHash,
                            houseNumber.getText(),
                            postCode.getText(),
                            1 // TODO: Change this to the correct bank details ID
                        );

                        DatabaseOperation.CreatePerson(newPerson);
                        System.out.println("Successfully created new person");

                        // Close register window and open login window
                        Window w = SwingUtilities.getWindowAncestor(Register.this);
                        w.dispose();
//                        Login.startLogin();
                        JOptionPane.showMessageDialog(null, "Registration successful");
                    } catch (SQLException throwables) {
                        JOptionPane.showMessageDialog(null, throwables.getMessage());
                    } finally {
                        db.closeConnection();
                    }
                    

                }
            }
    
        });
    }

    private boolean validateCompulsoryInput() {
        if (forename.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Forename cannot be empty");
            return false;
        }
        if (surname.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Surname cannot be empty");
            return false;
        }
        if (email.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Email cannot be empty");
            return false;
        }
        if (password.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password cannot be empty");
            return false;
        }
        if (passwordConfirmation.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password confirmation cannot be empty");
            return false;
        }
        if (!password.getText().equals(passwordConfirmation.getText())) {
            JOptionPane.showMessageDialog(null, "Passwords do not match");
            return false;
        }
        if (houseNumber.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "House number cannot be empty");
            return false;
        }
        if (streetName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Street name cannot be empty");
            return false;
        }
        if (cityName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "City name cannot be empty");
            return false;
        }
        if (postCode.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Postcode cannot be empty");
            return false;
        }
        try {
            DatabaseBridge db = DatabaseBridge.instance();
            db.openConnection();
            if (DatabaseOperation.GetPersonByEmail(email.getText()) != null) {
                JOptionPane.showMessageDialog(null, "Email already exists");
                return false;
            }
            db.closeConnection();
        } catch (SQLException throwables) {
            JOptionPane.showMessageDialog(null, throwables.getMessage());
        }
        return true;
    }

    private boolean validateFormat(){
        // regular expressions for email, postcode and password
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
        String postcodeRegex = "^[A-Z]{1,2}[0-9]{1,2}\s[A-Z]?[0-9][A-Z]{2}$";
        // 8-20 characters long, contain at least one digit, one upper case letter, one lower case letter and one special character
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!()]).{8,20}$";

        // Validate the fields with the regular expressions
        if (!email.getText().matches(emailRegex)) {
            JOptionPane.showMessageDialog(null, "Invalid email");
            return false;
        }
        if (!postCode.getText().matches(postcodeRegex)) {
            JOptionPane.showMessageDialog(null, "Invalid postcode");
            return false;
        }
        if (!password.getText().matches(passwordRegex)) {
            JOptionPane.showMessageDialog(null, "Password must be 8-20 characters long, contain at least one digit, one upper case letter, one lower case letter and one special character");
            return false;
        }
        return true;
    }


    public static void startRegister() {
        App app = new App();
        JFrame frame = new JFrame ("Register");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (new Register(app));
        frame.pack();
        frame.setVisible (true);
    }
}
