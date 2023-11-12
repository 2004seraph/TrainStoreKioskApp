package entity;
import java.util.Arrays;
import java.util.List;

import db.DatabaseRecord;

public class Address implements DatabaseRecord{
    private String houseNumber;
    private String streetName;
    private String cityName;
    private String postcode;

    public Address(String houseNumber, String streetName, String cityName, String postcode) {
        this.houseNumber = houseNumber;
        this.streetName = streetName;
        this.cityName = cityName;
        this.postcode = postcode;
    }

    @Override
    public List<Object> GetFields() {
        return Arrays.asList(
                houseNumber,
                streetName,
                cityName,
                postcode
        );
    }
}