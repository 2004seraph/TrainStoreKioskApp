package entity.user;

import db.DatabaseBridge;
import entity.BankDetail;
import entity.Address;

import java.security.InvalidKeyException;
import java.sql.SQLException;

public final class PersonCompressed {
    private Person person;
    private Address address;
    private BankDetail bankDetail;

    public Person getPerson() {
        return person;
    }
    public Address getAddress() {
        return address;
    }
    public BankDetail getBankDetail() {
        return bankDetail;
    }

    public PersonCompressed(Person person, Address address, BankDetail bankDetail) {
        this.person = person;
        this.address = address;
        this.bankDetail = bankDetail;
    }

    public static PersonCompressed getPersonalDetails(String email) throws SQLException, InvalidKeyException {
        try{
            Person person = Person.getPersonByEmail(email);
            if (person == null) {
                throw new SQLException("No person found with that email");
            }
            Address address = Address.getAddressById(person.getHouseNumber(), person.getPostCode());
            if (address == null) {
                throw new SQLException("No address found with that house number and postcode");
            }
            BankDetail bankDetail = BankDetail.getBankDetailsById(person.getBankDetailsId());
            return new PersonCompressed(person, address, bankDetail);
        } catch (SQLException e) {
            throw e;
        } catch (InvalidKeyException e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }
}
