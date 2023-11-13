package entity;

public class BankDetail {
    public static class BankAccountNotFoundException extends RuntimeException {
        public BankAccountNotFoundException(String message) {
            super(message);
        }
    }
    private int bankDetailID;
    private String cardName;
    private String cardNumber;
    private String expiryDate;
    private String securityCode;

}