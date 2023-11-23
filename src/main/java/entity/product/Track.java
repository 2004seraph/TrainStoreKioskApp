package entity.product;

public class Track extends Component {
    public enum Curvature {
        STRAIGHT,
        FIRST_RADIUS,
        SECOND_RADIUS,
        THIRD_RADIUS,
    }

    public Track(String name, int stock, Double price, String brand, int era, Gauge gauge) {
        super(name, stock, price, brand, era, gauge);
    }
}
