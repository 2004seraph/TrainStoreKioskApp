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

    private JTextField cardName;
    private JTextField cardHolderName;
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
        try {
            db.openConnection();
            forename.setText(person.getForename());
            surname.setText(person.getSurname());
            emailField.setText(person.getEmail());

            if (person.getAddress() != null) {
                houseNumber.setText(person.getAddress().getHouseNumber());
                street.setText(person.getAddress().getStreetName());
                city.setText(person.getAddress().getCityName());
                postCode.setText(person.getAddress().getPostcode());
            }

            JLabel bankDetailsTitle = new JLabel("<html><h2>Bank Details</h2></html>");
            gbc.gridx = 0;
            gbc.gridy = 9;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            add(bankDetailsTitle, gbc);

            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridwidth = 1;
            JLabel cardNameLabel = new JLabel("Card Name:");
            cardName = new JTextField(30);
            addField(gbc, cardNameLabel, cardName, 10);

            JLabel cardHolderNameLabel = new JLabel("Card Holder Name:");
            cardHolderName = new JTextField(30);
            addField(gbc, cardHolderNameLabel, cardHolderName, 11);

            JLabel cardNumberLabel = new JLabel("Card Number:");
            cardNumber = new JTextField(30);
            addField(gbc, cardNumberLabel, cardNumber, 12);

            JLabel expiryDateLabel = new JLabel("Expiry Date (yyyy-mm-dd):");
            expiryDate = new JTextField(30);
            addField(gbc, expiryDateLabel, expiryDate, 13);

            JLabel securityCodeLabel = new JLabel("Security Code:");
            securityCode = new JTextField(30);
            addField(gbc, securityCodeLabel, securityCode, 14);

            if (person.getBankDetail() != null) {
                cardName.setText(person.getBankDetail().getCardName());
                cardHolderName.setText(person.getBankDetail().getCardHolderName());
                cardNumber.setText(person.getBankDetail().getCardNumber());
                expiryDate.setText(person.getBankDetail().getExpiryDate().toString());
                securityCode.setText(person.getBankDetail().getSecurityCode());
            }

            gbc.gridy++;
            JButton updateButton = new JButton("Update Profile");
            add(updateButton, gbc);
            updateButton.addActionListener(e -> {
                updateDetails();
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
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

        String cardNameInput = cardName.getText();
        String cardHolderNameInput = cardHolderName.getText();
        String cardNumberInput = cardNumber.getText();
        String expiryInput = expiryDate.getText();
        String securityInput = securityCode.getText();

//        Validate the data
        try {
            Person.validatePersonalDetails(forenameInput, surnameInput, emailInput,  houseNumberInput, streetInput, cityInput, postCodeInput);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Invalid fields: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        try {
            BankDetail.validateBankDetails(cardNameInput, cardNumberInput, cardHolderNameInput, expiryInput, securityInput);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Invalid payment information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        DatabaseBridge db = DatabaseBridge.instance();
        try {
            db.openConnection();

            PreparedStatement emailLookup = db.prepareStatement("SELECT PersonId FROM Person WHERE email=?");
            emailLookup.setString(1, emailInput);
            ResultSet res = emailLookup.executeQuery();
            if (res.next() && res.getInt(1) != AppContext.getCurrentUser().getId()) {
                throw new IllegalArgumentException("Another user already exists with that email address");
            }

            BankDetail newBankDetails = BankDetail.createPaymentInfo(cardNameInput, cardNumberInput, cardHolderNameInput ,Date.valueOf(expiryInput), securityInput);
            AppContext.getCurrentUser().addNewBankDetails(newBankDetails);

            AppContext.getCurrentUser().updatePersonalDetails(emailInput, forenameInput, surnameInput, houseNumberInput, streetInput, cityInput, postCodeInput);

            JOptionPane.showMessageDialog(AppContext.getWindow(), "Personal details updated", "Notice", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(AppContext.getWindow(), "Personal details could not be updated: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            db.closeConnection();
        }
    }
}