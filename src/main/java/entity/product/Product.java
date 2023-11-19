package entity.product;

public class Product {

    private Integer productCode;
    private String name;
    protected Integer stockLevel;
    private Double price;
    private enum category {
            LOCOMOTIVES,
            CARRIAGES,
            WAGONS,
            TRACK,
            SCENERY,
            TRACK_PACKS,
            TRAIN_SETS
    }

    public static boolean InsertNewComponent() {
        // database insert
        return true;
    }
    public static boolean InsertNewBoxedSet() {
        // error checking 
        return true;


    }
}

