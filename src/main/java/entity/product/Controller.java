package entity.product;

public class Controller extends Component{
    public enum ControlType {
        ANALOG,
        DIGITAL,
    }

    private ControlType controlType;

    public Controller(String name, int stock, Double price, String brand, String era, ControlType controlType) {
        super(name, stock, price, brand, era, Gauge.NONE);
        this.controlType = controlType;
    }

    public ControlType getControlType() {
        return controlType;
    }
}