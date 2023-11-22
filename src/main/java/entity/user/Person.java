package entity.user;

import db.DatabaseBridge;
import db.DatabaseOperation;
import db.DatabaseRecord;
import entity.BankDetail;
import entity.StoreAttributes;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Person extends DatabaseOperation.Entity implements DatabaseRecord {
    public static class PersonNotFoundException extends RuntimeException {
        public PersonNotFoundException(String msg) {
            super(msg);
        }
    }

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

    public StoreAttributes.Role getRole() { return role; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return forename + " " + surname; }

    public static void main(String[] args){
        System.out.println("Hello World");
    }

    /**
     * This blank constructor is for creating a brand-new Person (not in the database yet)
     */
    private Person() {

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
    private Person(
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

    /**
     * Returns a result set of every person in the database
     * @return A resultSet
     */
    public static ResultSet getAllPersons() {
        try {
            openConnection();
            PreparedStatement query = prepareStatement("SELECT * FROM Person");
            return query.executeQuery();
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to fetch all persons", e);
            throw new RuntimeException();
        } finally {
            closeConnection();
        }
    }

    /**
     * Gets a person from the database using their email
     * @param email A string of an email, can be valid or invalid
     * @return A Person object with all of its fields set, or null if there was no one with that email
     * @throws SQLException
     */
    public static Person getPersonByEmail(
            String email
    ) throws SQLException {
        try (PreparedStatement personQuery = prepareStatement("SELECT * FROM Person WHERE email=?");
             PreparedStatement roleQuery = prepareStatement("SELECT * FROM Role WHERE personId=?");
        ) {
            personQuery.setString(1, email);
            ResultSet res = personQuery.executeQuery();

            Person person;
            if (res.next()) {
                int id = res.getInt(1);
                roleQuery.setInt(1, id);
                ResultSet roles = roleQuery.executeQuery();

                StoreAttributes.Role userRole = StoreAttributes.Role.USER;
                // get the highest priviledge role this user has and use that
                while (roles.next()) {
                    StoreAttributes.Role roleValue = StoreAttributes.Role.valueOf(roles.getString(2));
                    if (roleValue.getLevel() > userRole.getLevel())
                        userRole = roleValue;
                }

                person = new Person(
                        id,                            // id
                        res.getString(2),   // forename
                        res.getString(3),   // surname
                        res.getString(4),   // email
                        res.getString(5),   // password (this is horrible)
                        res.getString(6),   // houseName
                        res.getString(7),   // postcode
                        res.getInt(8),      // bank details
                        userRole
                );

                return person;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Inserts a Person object into the database
     * @param person A non-null person that doesn't have any null fields either
     * @return Whether the insertion was successful or failed due to someone already having that email address
     * @throws SQLException
     */
    public static Boolean createPerson(Person person) throws SQLException {
        // do not insert this person if an account with their email already exists
        if (getPersonByEmail(person.getEmail()) != null) {
            return false;
        }

        setAutoCommit(false);
        int id = -1;

        try (PreparedStatement s = prepareStatement("INSERT INTO Person VALUES (default,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement r = prepareStatement("INSERT INTO Role VALUES (?,?)");
        ) {

            Object[] fields = person.getFields().toArray();

            s.setString(1, (String) fields[0]); // forename
            s.setString(2, (String) fields[1]); // surname
            s.setString(3, (String) fields[2]); // email
            s.setString(4, (String) fields[3]); // password
            s.setString(5, (String) fields[4]); // housename
            s.setString(6, (String) fields[5]); // postcode
            if ((Integer)fields[6] != -1) {                  // paymentid
                s.setInt(7, (Integer) fields[6]);
            } else {
                s.setNull(7, Types.INTEGER);
            }
            s.executeUpdate();

            ResultSet rs = s.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                throw new InternalError("Failed to insert into Person table");
            }

            r.setInt(1, id);
            r.setString(2, person.getRole().name());
            r.executeUpdate();

            commit();
        } catch (SQLException | InternalError e) {
            DatabaseBridge.databaseError("Failed to insert new user", e);
            rollback();
            throw e;
        } finally {
            setAutoCommit(true);
        }

        return true;
    }

    /**
     * Assuming you already have the person entity you can update their role
     * @param person Person entity
     * @param newRole Either USER, STAFF or MANAGER
     * @return whether operation was successful
     * @throws SQLException
     */
    public static boolean updateUserRole(Person person, StoreAttributes.Role newRole) throws SQLException {
        try (PreparedStatement query = prepareStatement("""
                UPDATE Role, Person
                LEFT JOIN Role R ON Person.PersonId = R.personId
                SET R.role = ?
                WHERE email = ?
                """)) {
            query.setString(1, newRole.toString());
            query.setString(2, person.getEmail());

            return query.executeUpdate() > 0;
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to update user with email ["+person.getEmail()+"] to role ["+newRole.toString()+"]", e);
            throw e;
        }
    }

    @Override
    public List<Object> getFields() {
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

