import db.DatabaseBridge;
import db.DatabaseOperation;
import gui.*;

import entity.user.*;
import controllers.*;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws Exception {

        DatabaseBridge db = DatabaseBridge.instance(); //Can be called wherever you need a reference (it's a static method)
        DatabaseOperation.SetConnection(db); //Only needs to be called once with a DatabaseBridge reference and never again
        try {
            db.openConnection();

            Dashboard.generateLoginRegister();

            // Sam has already been created in the database (you can go see it in workbench).
            // I had to add an address and payment details record manually using the workbench.
            Person newPerson = new Person(
                    "SamNoPayment",
                    "Seraph",
                    "sam_no_paymentid@sheffield.ac.uk",
                    "password123",
                    
                    "21",         // THESE ARE REAL PRIMARY KEYS IN THE DB
                    "SHR982",      // THESE ARE REAL PRIMARY KEYS IN THE DB
                    -1         // THESE ARE REAL PRIMARY KEYS IN THE DB
            );
            System.out.println("Successfully added new person (if false they may already be present in the db): " + DatabaseOperation.CreatePerson(newPerson));

            Person sam = DatabaseOperation.GetPersonByEmail("sam_no_paymentid@sheffield.ac.uk");
            System.out.println("Successfully retrieved that new person: " + (sam != null));

        } catch (Throwable e) {
            System.out.println("Query/operation error");
            throw e;
        } finally {
            db.closeConnection();
        }
    }

}
