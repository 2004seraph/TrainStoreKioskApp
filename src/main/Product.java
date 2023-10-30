import java.util.Locale.Category;

public class Product {

    private Integer productCode;
    private String name;
    protected Integer stockLevel;
    private Double price;
    private enum category {
            LOCOMOTIVES,
            CARRAIGES,
            WAGONS,
            TRACK,
            SCENERY,
            TRACK_PACKS,
            TRAIN_SETS
    }

    public static boolean InsertNewComponent() {
        // database insetrt
        return true;
    }
    public static boolean InsertNewBoxedSet() {
        // error checking 
        return true;


    }
}

