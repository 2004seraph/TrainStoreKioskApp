import db.DatabaseBridge;
import db.DatabaseOperation;
import gui.*;

public class Main {

    public static void main(String[] args) throws Exception {
        //Only needs to be called once with a DatabaseBridge reference and never again
        DatabaseOperation.setConnection(DatabaseBridge.instance());

        App app = new App();
    }
}
