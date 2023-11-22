package entity.product;

public class Component extends Product{
    private String brand;
    private Integer era;

    public Component(String name, int stock, Double price, String brand, int era) {
        super(name, stock, price);
        this.brand = brand;
        this.era = era;
    }

    private enum gauge {
        OOGAUGE,
        TTGUAGE,
        NGAUGE,
    }


}