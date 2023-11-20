package entity;

import db.DatabaseOperation;
import db.DatabaseRecord;

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

    public List<Object> GetFields() {
        List<Object> list = Arrays.asList(
                cardName,
                cardNumber,
                expiryDate,
                securityCode
        );
        return list;
    }
}