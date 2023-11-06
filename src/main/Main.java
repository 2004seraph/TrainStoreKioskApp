import db.DatabaseBridge;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        DatabaseBridge db = DatabaseBridge.Instance();
        try {
            db.openConnection();
        } catch (Throwable e) {
            DatabaseBridge.databaseError("Query/operation error", e);
        } finally {
            db.closeConnection();
        }
    }
}
