package store;

public class Person {
    private int PersonID;
    private String forename;
    private String surname;
    private String email;
    private String password;

//  Link to Store.BankDetail and Store.Address
    private BankDetail bankDetail;
    private Address address;


    public static void main(String[]args){
        System.out.println("Hello World");
    }
}

