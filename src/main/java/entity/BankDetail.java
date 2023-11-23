package entity;

import controllers.AppContext;
import db.DatabaseBridge;
import db.DatabaseOperation;
import db.DatabaseRecord;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import utils.Crypto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.InvalidKeyException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BankDetail extends DatabaseOperation.Entity implements DatabaseRecord {
    public static class BankAccountNotFoundException extends RuntimeException {
        public BankAccountNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidBankDetailsException extends Exception {
        public InvalidBankDetailsException(String message) {
            super(message);
        }
    }
    private int bankDetailID = -1;
    private String cardName;
    private String cardNumber;
    private Date expiryDate;
    private String securityCode;


    public String getCardName() {
        return cardName;
    }
    public String getCardNumber() {
        return cardNumber;
    }
    public Date getExpiryDate() {
        return expiryDate;
    }
    public String getSecurityCode() {
        return securityCode;
    }

    public BankDetail(String cardName, String cardNumber, Date expiryDate, String securityCode) {
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.securityCode = securityCode;
    }

    public BankDetail(int bankDetailID, String cardName, String cardNumber, Date expiryDate, String securityCode) {
        this.bankDetailID = bankDetailID;
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.securityCode = securityCode;
    }


    /**
     * Creates an entry in the database for a new set of bank details, it also returns a constructed class representation.
     * @param cardNumber
     * @param expiryDate
     * @param securityCode
     * @return A new instance of BankDetail referencing an entry in the DB
     * @throws SQLException
     * @throws InvalidBankDetailsException
     */
    public static BankDetail createPaymentInfo(String cardNumber, java.sql.Date expiryDate, String securityCode)
            throws SQLException, InvalidBankDetailsException {
        int id = -1;
        boolean isCardValid = LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(cardNumber);
        if (!isCardValid) {
            throw new InvalidBankDetailsException("Card number invalid, failed Luhn check ["+cardNumber+"]");
        }
        // Expiry date should be in the format MM/YY
        if (expiryDate.before(new java.util.Date())) {
            throw new InvalidBankDetailsException("Card is expired ["+expiryDate.toLocalDate()+"]");
        }

        if (securityCode.length() != 3) {
            throw new InvalidBankDetailsException("Security code was an invalid length ["+securityCode+"]");
        }

        String cardName = "Card ending in " + cardNumber.substring(cardNumber.length() - 4);
        try (PreparedStatement cardQuery = prepareStatement("INSERT INTO CardDetails VALUES (?, ?, ?, ?")) {
            byte[] encryptionKey = AppContext.getEncryptionKey();
            String encryptedCardNumber = Crypto.encryptString(cardNumber, encryptionKey);
            String encryptedSecurityCode = Crypto.encryptString(securityCode, encryptionKey);

            cardQuery.setString(1, cardName);
            cardQuery.setString(2, encryptedCardNumber);
            cardQuery.setDate(3, expiryDate);
            cardQuery.setString(4, encryptedSecurityCode);

            cardQuery.executeUpdate();
            ResultSet rs = cardQuery.getGeneratedKeys();

            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                throw new InternalError("Failed to insert into BankDetails table");
            }

            return new BankDetail(id, cardName, cardNumber, expiryDate, securityCode);
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to insert new payment info ["+cardName+"]", e);
            throw e;
        } catch (InvalidKeyException e) {
            Crypto.cryptoError("Error whilst encrypting card number, encryption key was invalid", e);
            throw new RuntimeException(e);
        }
    }

    public static BankDetail getBankDetailsById(int id) throws SQLException, InvalidKeyException {
        try(PreparedStatement bankQuery = prepareStatement("SELECT * FROM BankDetails WHERE paymentId = ?")) {
            bankQuery.setInt(1, id);
            ResultSet rs = bankQuery.executeQuery();

            if (!rs.next()) {
                throw new BankAccountNotFoundException("Failed to find bank details with id ["+id+"]");
            }

            byte[] encryptionKey = AppContext.getEncryptionKey();
            String decryptedCardNumber = Crypto.decryptString(rs.getString("cardNumber"), encryptionKey);
            String decryptedSecurityCode = Crypto.decryptString(rs.getString("securityCode"), encryptionKey);

            return new BankDetail(
                    rs.getInt("paymentId"),
                    rs.getString("cardName"),
                    decryptedCardNumber,
                    rs.getDate("expiryDate"),
                    decryptedSecurityCode
            );
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to fetch bank details with id ["+id+"]", e);
            throw e;
        } catch (InvalidKeyException e) {
            // Thrown when the user tries to decrypt another user's card with an invalid encryption key
            // I.e. they are trying to decrypt a card that isn't theirs
            Crypto.cryptoError("User tried to decrypt card with id ["+id+"] but they used the wrong encryption key", e);
            throw e;
        }
    }

//    Create a JFrame pop up to collect the bank details from the user
    public static BankDetail getBankDetailsFromUser() {
        // Create a new JFrame
        JFrame frame = new JFrame("Bank Details");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create the labels
        JLabel cardNameLabel = new JLabel("Card Name");
        JLabel cardNumberLabel = new JLabel("Card Number");
        JLabel expiryDateLabel = new JLabel("Expiry Date");
        JLabel securityCodeLabel = new JLabel("Security Code");

        // Create the text fields
        JTextField cardName = new JTextField(25);
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
        frame.add(cardNameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(cardName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(cardNumberLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(cardNumber, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(expiryDateLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        frame.add(expiryDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(securityCodeLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        frame.add(securityCode, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(submitButton, gbc);

        frame.setVisible(true);

        // When clicking submitButton, close this window and open Register window
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cardNameInput = cardName.getText();
                String cardNumberInput = cardNumber.getText();
                String expiryDateInput = expiryDate.getText();
                String securityCodeInput = securityCode.getText();
            }
        });
        return null;
    }

    @Override
    public List<Object> getFields() {
        return Arrays.asList(
                cardName,
                cardNumber,
                expiryDate,
                securityCode
        );
    }
}