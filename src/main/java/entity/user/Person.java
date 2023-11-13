package entity.user;

import db.DatabaseBridge;
import db.DatabaseOperation;
import db.DatabaseRecord;
import entity.BankDetail;
import entity.StoreAttributes;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public String getPassword() { return password; }

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


    public boolean setBankAccountID(int id) {
        try (PreparedStatement query = prepareStatement("SELECT cardName FROM BankDetails WHERE paymentId = ?")){
            query.setInt(1, id);
            boolean found = query.execute();
            if (!found) {
                throw new BankDetail.BankAccountNotFoundException("Could not find bank details with ID ["+id+"]");
            }

        } catch (SQLException e) {
            DatabaseBridge.databaseError("Could not fetch card details for card ID [" + id + "]", e );
            return false;
        } catch (BankDetail.BankAccountNotFoundException e) {
            throw new BankDetail.BankAccountNotFoundException(e.getMessage());
        }


        try (PreparedStatement update = prepareStatement("UPDATE Person UPDATE paymentId = ? WHERE PersonId = ?")) {
            update.setInt(1, id);
            update.setInt(2, personID);

            return update.getUpdateCount() > 0;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Could not update [" + this.email + "]'s payment information", e);
            return false;
        }
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

