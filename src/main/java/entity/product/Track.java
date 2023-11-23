package entity.product;

public class Track extends Component {
    public enum Curvature {
        STRAIGHT,
        FIRST_RADIUS,
        SECOND_RADIUS,
        THIRD_RADIUS,
    }

    private Curvature curvature;

    public Track(String name, int stock, Double price, String brand, int era, Gauge gauge, Curvature curvature) {
        super(name, stock, price, brand, era, gauge);

        this.curvature = curvature;
    }
}
