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

}