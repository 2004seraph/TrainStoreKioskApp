package entity.product;

public class Component extends Product{
    public static class ComponentNotFoundException extends RuntimeException {
        public ComponentNotFoundException(String msg) {super(msg);}
    }
    private String brand;
    private Integer era;

    public enum Gauge {
        NONE,
        OOGAUGE,
        TTGUAGE
    }

    private Gauge gauge;

    public Component(String name, int stock, Double price, String brand, int era, Gauge gauge) {
        super(name, stock, price);
        this.brand = brand;
        this.era = era;
        this.gauge = gauge;
    }

    public String getBrand() {
        return brand;
    }

    public Integer getEra() {
        return era;
    }

    public Gauge getGauge() {
        return gauge;
    }
}