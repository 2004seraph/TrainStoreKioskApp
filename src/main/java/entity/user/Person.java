package entity.user;

import db.DatabaseOperation;
import db.DatabaseRecord;
import entity.StoreAttributes;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Person extends DatabaseOperation.Entity implements DatabaseRecord {

    // -1 means the Person exists only in Java and hasn't had an entry made in the db for it
    private int personID = -1;
    private String forename;
    private String surname;
    private String email;
    private String password;
    private String houseName;
    private String postCode;
    private int bankDetailsID;

    private StoreAttributes.Role role = StoreAttributes.Role.USER;

    public StoreAttributes.Role getRole() {
        return role;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    public static void main(String[] args){
        System.out.println("Hello World");
    }

    /**
     * This blank constructor is for creating a brand-new Person (not in the database yet)
     */
    public Person() {

    }

    /**
     * Another constructor to use when creating a brand-new person,
     * note that the id is not included (generated when inserting)
     * @param forename
     * @param surname
     * @param email
     * @param password
     * @param houseName
     * @param postCode
     * @param bankDetailsID
     */
    public Person(
            String forename,
            String surname,
            String email,
            String password,
            String houseName,
            String postCode,
            int bankDetailsID
    ) {
        this.forename =      forename;
        this.surname =       surname;
        this.email =         email;
        this.password =      password;
        this.houseName =     houseName;
        this.postCode =      postCode;
        this.bankDetailsID = bankDetailsID;
    }

    /**
     * The constructor to use when retrieving a pre-existing person from the database,
     * Note the inclusion of the id
     * @param id
     * @param forename
     * @param surname
     * @param email
     * @param password
     * @param houseName
     * @param postCode
     * @param bankDetailsID
     */
    public Person(
            int id,
            String forename,
            String surname,
            String email,
            String password,
            String houseName,
            String postCode,
            int bankDetailsID,
            StoreAttributes.Role role
    ) {
        this.personID =      id;
        this.forename =      forename;
        this.surname =       surname;
        this.email =         email;
        this.password =      password;
        this.houseName =     houseName;
        this.postCode =      postCode;
        this.bankDetailsID = bankDetailsID;
        this.role = role;
    }

    @Override
    public List<Object> GetFields() {
        // note that the AUTO_INCREMENT primary key is not included, this method is used for inserting
        // these also appear in the same order as the table fields (important)
        List<Object> list = Arrays.asList(
                forename,
                surname,
                email,
                password,
                houseName,
                postCode,
                bankDetailsID
        );
        return list;
    }
}

