import db.DatabaseBridge;
import db.DatabaseOperation;
import gui.*;

public class Main {

    public static void main(String[] args) {
        //Only needs to be called once with a DatabaseBridge reference and never again
        DatabaseOperation.setConnection(DatabaseBridge.instance());

        try {
            App app = new App();
        } catch (Throwable e) {
            DatabaseBridge.instance().closeConnection();
            throw new RuntimeException(e);
        }
    }
}
