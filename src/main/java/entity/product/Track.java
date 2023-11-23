package entity.product;

public class Track extends Component {
    public enum Curvature {
        STRAIGHT,
        FIRST_RADIUS,
        SECOND_RADIUS,
        THIRD_RADIUS;

        public static Curvature get(String value) {
            return switch (value) {
                case "STRAIGHT" -> Curvature.STRAIGHT;
                case "FIRST_RADIUS" -> Curvature.FIRST_RADIUS;
                case "SECOND_RADIUS" -> Curvature.SECOND_RADIUS;
                case "THIRD_RADIUS" -> Curvature.THIRD_RADIUS;
                default -> throw new IllegalStateException("Unexpected value: " + value);
            };
        }
    }

    private Curvature curvature;

    public Track(String name, int stock, Double price, String brand, int era, Gauge gauge, Curvature curvature) {
        super(name, stock, price, brand, era, gauge);

        this.curvature = curvature;
    }

    public Curvature getCurvature() {
        return curvature;
    }
}
