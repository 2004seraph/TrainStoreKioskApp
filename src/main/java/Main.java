import db.DatabaseBridge;
import db.DatabaseOperation;
import gui.*;

import entity.user.*;

public class Main {

    public static void main(String[] args) throws Exception {

        DatabaseBridge db = DatabaseBridge.instance(); //Can be called wherever you need a reference (it's a static method)
        DatabaseOperation.SetConnection(db); //Only needs to be called once with a DatabaseBridge reference and never again

        App.loggedOutScreen();
    }

}
