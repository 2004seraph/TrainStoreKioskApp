package gui.person;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.DatabaseBridge;
import entity.BankDetail;
import entity.user.Person;
import controllers.AppContext;

public class Profile extends JPanel{

    private final JTextField forename = new JTextField(30);
    private final JTextField surname = new JTextField(30);
    private final JTextField emailField = new JTextField(30);
    private final JTextField houseNumber = new JTextField(30);
    private final JTextField street = new JTextField(30);
    private final JTextField city = new JTextField(30);
    private final JTextField postCode = new JTextField(30);

    private JTextField cardNumber;
    private JTextField expiryDate;
    private JTextField securityCode;

    private BankDetail bankDetail;

    public Profile() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 10, 1, 1);

//        Add the title
        JLabel title = new JLabel("<html><h1>Profile</h1></html>");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        //    Personal Details
        JLabel forenameLabel = new JLabel("Forename:");
        addField(gbc, forenameLabel, forename, 1);
        JLabel surnameLabel = new JLabel("Surname:");
        addField(gbc, surnameLabel, surname, 2);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel emailLabel = new JLabel("Email:");
        add(emailLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(emailField, gbc);

        JLabel addressTitle = new JLabel("<html><h2>Address</h2></html>");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(addressTitle, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        //    Address
        JLabel houseNumberLabel = new JLabel("House Number:");
        addField(gbc, houseNumberLabel, houseNumber, 5);
        JLabel streetLabel = new JLabel("Street:");
        addField(gbc, streetLabel, street, 6);
        JLabel cityLabel = new JLabel("City:");
        addField(gbc, cityLabel, city, 7);
        JLabel postCodeLabel = new JLabel("PostCode:");
        addField(gbc, postCodeLabel, postCode, 8);;

        Person person = AppContext.getCurrentUser();
        DatabaseBridge db = DatabaseBridge.instance();
        //            TODO: HANDLE BANK DETAILS IF NULL
        try {
            db.openConnection();
            forename.setText(person.getForename());
            surname.setText(person.getSurname());
            emailField.setText(person.getEmail());
            houseNumber.setText(person.getAddress().getHouseNumber());
            street.setText(person.getAddress().getStreetName());
            city.setText(person.getAddress().getCityName());
            postCode.setText(person.getAddress().getPostcode());

            if (person.getBankDetail() != null) {
//                Add bank details to the profile
//                Add bank details title
                JLabel bankDetailsTitle = new JLabel("<html><h2>Bank Details</h2></html>");
                gbc.gridx = 0;
                gbc.gridy = 9;
                gbc.gridwidth = 2;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.CENTER;
                add(bankDetailsTitle, gbc);

                gbc.fill = GridBagConstraints.NONE;
                gbc.anchor = GridBagConstraints.WEST;
                //    Bank Details
                JLabel cardNumberLabel = new JLabel("Card Number:");
                cardNumber = new JTextField(30);
                addField(gbc, cardNumberLabel, cardNumber, 10);

                JLabel expiryDateLabel = new JLabel("Expiry Date:");
                expiryDate = new JTextField(30);
                addField(gbc, expiryDateLabel, expiryDate, 11);

                JLabel securityCodeLabel = new JLabel("Security Code:");
                securityCode = new JTextField(30);
                addField(gbc, securityCodeLabel, securityCode, 12);

                cardNumber.setText(person.getBankDetail().getCardNumber());
                expiryDate.setText(person.getBankDetail().getExpiryDate().toString());
                securityCode.setText(person.getBankDetail().getSecurityCode());

//                For the update button
                gbc.gridx = 0;
                gbc.gridy = 13;
                gbc.gridwidth = 2;
                gbc.fill = GridBagConstraints.NONE;
                gbc.anchor = GridBagConstraints.CENTER;
            } else {
                gbc.gridx = 1;
                gbc.gridy = 10;
                JButton addBankDetailsButton = new JButton("ADD BANK DETAILS");
                add(addBankDetailsButton, gbc);
//                For the update button
                gbc.gridx = 0;
                gbc.gridy = 10;

                addBankDetailsButton.addActionListener(e -> {
                    getBankDetailsFromUser();
                });
            }
            //    Buttons
            JButton updateButton = new JButton("UPDATE");
            add(updateButton, gbc);
            updateButton.addActionListener(e -> {
                updateDetails();
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            db.closeConnection();
        }
    }

    private void addField(GridBagConstraints gbc, JLabel label, JTextField textField, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        add(textField, gbc);
    }

    /**
     * Updates the user's personal details
     */
    public void updateDetails(){
        String emailInput = emailField.getText();

        String forenameInput = forename.getText();
        String surnameInput = surname.getText();

        String houseNumberInput = houseNumber.getText();
        String streetInput = street.getText();
        String cityInput = city.getText();
        String postCodeInput = postCode.getText();

        String cardNumberInput = cardNumber.getText();
        String expiryInput = expiryDate.getText();
        String securityInput = securityCode.getText();

//        Validate the data
        try {
            Person.validatePersonalDetails(forenameInput, surnameInput, houseNumberInput, streetInput, cityInput, postCodeInput);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Invalid fields: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        try {
            BankDetail.validateBankDetails(cardNumberInput, expiryInput, securityInput);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Invalid payment information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        DatabaseBridge db = DatabaseBridge.instance();
        try {
            db.openConnection();

            PreparedStatement emailLookup = db.prepareStatement("SELECT email FROM Person WHERE email=?");
            emailLookup.setString(1, emailInput);
            ResultSet res = emailLookup.executeQuery();
            if (res.next()) {
                throw new IllegalArgumentException("Another user already exists with that email address");
            }

            BankDetail newBankDetails = BankDetail.createPaymentInfo(cardNumberInput, Date.valueOf(expiryInput), securityInput);
            AppContext.getCurrentUser().addNewBankDetails(newBankDetails);

            AppContext.getCurrentUser().updatePersonalDetails(emailInput, forenameInput, surnameInput, houseNumberInput, streetInput, cityInput, postCodeInput);

            JOptionPane.showMessageDialog(AppContext.getWindow(), "Personal details updated", "Notice", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Personal details could not be updated: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            db.closeConnection();
        }

    }

    /**
     * Creates a new JFrame as a pop up that allows the user to enter their bank details
     */
    public void getBankDetailsFromUser() {
        // Create a new JFrame
        JDialog frame = new JDialog(AppContext.getWindow());
        frame.setTitle("Bank Details");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create the labels
        JLabel cardNumberLabel = new JLabel("Card Number");
        JLabel expiryDateLabel = new JLabel("Expiry Date (yyyy-mm-dd)");
        JLabel securityCodeLabel = new JLabel("Security Code");

        // Create the text fields
        JTextField cardNumber = new JTextField(25);
        JTextField expiryDate = new JTextField(25);
        JTextField securityCode = new JTextField(25);

        // Create the button
        JButton submitButton = new JButton("Submit");

//        Add the title
        JLabel title = new JLabel("<html><h1>Bank Details</h1></html>");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(title, gbc);

        // Add the labels and text fields to the JFrame
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(cardNumberLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(cardNumber, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(expiryDateLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(expiryDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(securityCodeLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        frame.add(securityCode, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(submitButton, gbc);

        frame.setVisible(true);

        // When clicking submitButton, close this window and open Register window
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String cardNumberInput = cardNumber.getText();
                String expiryDateInput = expiryDate.getText();
                String securityCodeInput = securityCode.getText();

                Date expiryDate = Date.valueOf(expiryDateInput);

                try{
                   BankDetail.validateBankDetails(cardNumberInput, expiryDateInput, securityCodeInput);
                } catch (BankDetail.InvalidBankDetailsException exception) {
                    JOptionPane.showMessageDialog(AppContext.getWindow(), exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                DatabaseBridge db = DatabaseBridge.instance();
                try{
                    db.openConnection();
                    bankDetail = BankDetail.createPaymentInfo(cardNumberInput, expiryDate, securityCodeInput);
//                    Add the bank details to the user
                    AppContext.getCurrentUser().addNewBankDetails(bankDetail);
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Bank Details Added");
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(AppContext.getWindow(), exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    db.closeConnection();
                    frame.dispose();
                }

                // reload the current state
                try {
                    db.openConnection();
                    AppContext.setCurrentUser(Person.getPersonByID(AppContext.getCurrentUser().getId()));
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                } finally {
                    db.closeConnection();
                }

                revalidate();
                repaint();
            }
        });
    }
}