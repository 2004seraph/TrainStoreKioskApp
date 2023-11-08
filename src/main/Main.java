import db.DatabaseBridge;
import db.DatabaseOperations;
import gui.*;

import entity.user.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        DatabaseBridge db = DatabaseBridge.Instance(); // Can be called wherever you need a reference (it's a static method)
        DatabaseOperations.SetConnection(db); // Only needs to be called once with a DatabaseBridge reference
        try {
            db.openConnection();

            Login.startLogin();

            Person user = DatabaseOperations.GetPersonByLoginCredentials(
                            "bobby-mong@sheffield.ac.uk",
                            "amogus123");

        } catch (Throwable e) {
            System.out.println("Query/operation error");
        } finally {
            db.closeConnection();
        }
    }
}
