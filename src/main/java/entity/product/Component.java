package entity.product;

public class Component extends Product{
    private String brand;
    private Integer era;

    public enum Gauge {
        OOGAUGE,
        TTGUAGE,
        NGAUGE,
        NONE
    }

    private Gauge gauge;

    public Component(String name, int stock, Double price, String brand, int era, Gauge gauge) {
        super(name, stock, price);
        this.brand = brand;
        this.era = era;
        this.gauge = gauge;
    }


}