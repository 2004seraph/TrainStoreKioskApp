package gui;

import javax.swing.*;
import java.awt.*;

import db.DatabaseBridge;
import entity.user.Person;
import entity.user.PersonCompressed;
import controllers.AppContext;

public class Profile extends JPanel{

//    Personal Details
    private JLabel forenameLabel = new JLabel("Forename:");
    private JTextField forename = new JTextField(30);
    private JLabel surnameLabel = new JLabel("Surname:");
    private JTextField surname = new JTextField(30);
    private JLabel emailLabel = new JLabel("Email:");
    private JLabel email = new JLabel();
//    Address
    private JLabel houseNumberLabel = new JLabel("House Number:");
    private JTextField houseNumber = new JTextField(30);
    private JLabel streetLabel = new JLabel("Street:");
    private JTextField street = new JTextField(30);
    private JLabel cityLabel = new JLabel("City:");
    private JTextField city = new JTextField(30);
    private JLabel postCodeLabel = new JLabel("PostCode:");
    private JTextField postCode = new JTextField(30);
//    Bank Details
    private JLabel cardNameLabel = new JLabel("Card Name:");
    private JTextField cardName = new JTextField(30);
    private JLabel cardNumberLabel = new JLabel("Card Number:");
    private JTextField cardNumber = new JTextField(30);
    private JLabel expiryDateLabel = new JLabel("Expiry Date:");
    private JTextField expiryDate = new JTextField(30);
    private JLabel securityCodeLabel = new JLabel("Security Code:");
    private JTextField securityCode = new JTextField(30);
//    Buttons
    private JButton updateButton = new JButton("UPDATE");
    private JButton addBankDetailsButton = new JButton("ADD BANK DETAILS");


    public Profile() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 10, 1, 1);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addField(gbc, forenameLabel, forename, 0);
        addField(gbc, surnameLabel, surname, 1);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(emailLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(email, gbc);

        addField(gbc, houseNumberLabel, houseNumber, 3);
        addField(gbc, streetLabel, street, 4);
        addField(gbc, cityLabel, city, 5);
        addField(gbc, postCodeLabel, postCode, 6);;

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(updateButton, gbc);

        Person person = AppContext.getCurrentUser();
        DatabaseBridge db = DatabaseBridge.instance();
        //            TODO: HANDLE BANK DETAILS IF NULL
        try {
            db.openConnection();
            PersonCompressed personCompressed = PersonCompressed.getPersonalDetails(person.getEmail());
            forename.setText(personCompressed.getPerson().getForename());
            surname.setText(personCompressed.getPerson().getSurname());
            email.setText(personCompressed.getPerson().getEmail());
            houseNumber.setText(personCompressed.getAddress().getHouseNumber());
            street.setText(personCompressed.getAddress().getStreetName());
            city.setText(personCompressed.getAddress().getCityName());
            postCode.setText(personCompressed.getAddress().getPostcode());
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


}
