package gui;

import java.awt.*;
import javax.swing.*;

public class Register extends JPanel{
    private JLabel registerLabel;
    private JTextField forename;
    private JLabel forenameLabel;
    private JLabel surnameLabel;
    private JPasswordField surname;
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

    public Register() {
        //construct components
        registerLabel = new JLabel ("<html><h1>REGISTER</h1></html>");
        forename = new JTextField (5);
        forenameLabel = new JLabel ("Forename:");
        surnameLabel = new JLabel ("Surname:");
        surname = new JPasswordField (5);
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
        setPreferredSize (new Dimension (752, 649));
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
    }

    public static void main (String[] args) {
        JFrame frame = new JFrame ("Register");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (new Register());
        frame.pack();
        frame.setVisible (true);
    }
}
