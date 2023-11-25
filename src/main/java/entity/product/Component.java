package entity.product;

public class Component extends Product{
    public static class ComponentNotFoundException extends RuntimeException {
        public ComponentNotFoundException(String msg) {super(msg);}
    }
    private String brand;
    private String era;

    public enum Gauge {
        NONE,
        OOGAUGE,
        TTGUAGE,
        NGAUGE
    }

    private Gauge gauge;

    public Component(String name, int stock, Double price, String brand, String era, Gauge gauge) {
        super(name, stock, price);
        this.brand = brand;
        this.era = era;
        this.gauge = gauge;
    }

    public String getBrand() {
        return brand;
    }

    public String getEra() {
        return era;
    }

    public Gauge getGauge() {
        return gauge;
    }
}