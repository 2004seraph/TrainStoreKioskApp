class public Product {

    private Integer productCode;
    private String name;
    protected Integer stockLevel;
    private Double price;
    enum private category {
            LOCOMOTIVES,
            CARRAIGES,
            WAGONS,
            TRACK,
            SCENERY,
            TRACK_PACKS,
            TRAIN_SETS
    }

}