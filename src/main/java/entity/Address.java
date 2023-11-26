package entity;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import db.DatabaseBridge;
import db.DatabaseOperation;
import db.DatabaseRecord;

public class Address extends DatabaseOperation.Entity implements DatabaseRecord{
    private String houseNumber;
    private String streetName;
    private String cityName;
    private String postcode;

    public String getHouseNumber() {
        return houseNumber;
    }
    public String getStreetName() {
        return streetName;
    }
    public String getCityName() {
        return cityName;
    }
    public String getPostcode() {
        return postcode;
    }

    public Address(String houseNumber, String streetName, String cityName, String postcode) {
        this.houseNumber = houseNumber;
        this.streetName = streetName;
        this.cityName = cityName;
        this.postcode = postcode;
    }

    public static boolean validatePostcode(String postcode) {
        return postcode.matches("^[a-zA-Z0-9]{6}$");
    }

    /**
     * Inserts a new address into the database
     * @param address A non-null address that doesn't have any null fields either
     * @return Whether the insertion was successful or failed due to someone already having that house address
     * @throws SQLException
     */
    public static Boolean CreateAddress(Address address) throws SQLException {
        // Check if this address already exists using the primary key houseNumber and postCode

        try (PreparedStatement s = prepareStatement("SELECT * FROM Address WHERE houseNumber=? AND postCode=?")){
            Object [] fields = address.getFields().toArray();

            s.setString(1, fields[0].toString());
            s.setString(2, fields[3].toString());

            ResultSet res = s.executeQuery();
            if(!res.next()){
                // Create a new address to the database
                try (PreparedStatement r = prepareStatement("INSERT INTO Address VALUES (?,?,?,?)")){
                    r.setString(1, fields[0].toString());
                    r.setString(2, fields[1].toString());
                    r.setString(3, fields[2].toString());
                    r.setString(4, fields[3].toString());
                    r.executeUpdate();
                } catch (SQLException e) {
                    DatabaseBridge.databaseError("Failed to insert new address");
                    throw e;
                }
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to insert new address");
            throw e;
        }
        return true;
    }

    public static Address getAddressById(String houseNumber, String postcode) throws SQLException {
        try (PreparedStatement s = prepareStatement("SELECT * FROM Address WHERE houseNumber=? AND postCode=?")){
            s.setString(1, houseNumber);
            s.setString(2, postcode);
            ResultSet res = s.executeQuery();
            if(res.next()){
                return new Address(
                        res.getString("houseNumber"),
                        res.getString("streetName"),
                        res.getString("cityName"),
                        res.getString("postCode")
                );
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to get address");
            throw e;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(houseNumber);
        sb.append(" ");
        sb.append(streetName);
        sb.append(", ");
        sb.append(postcode);
        sb.append(", ");
        sb.append(cityName);
        return sb.toString();
    }

    @Override
    public List<Object> getFields() {
        return Arrays.asList(
                houseNumber,
                streetName,
                cityName,
                postcode
        );
    }

}