package entity.product;

public class Controller extends Component{
    public enum ControlType {
        ANALOG,
        DIGITAL,
    }

    private ControlType controlType;

    public Controller(String name, int stock, Double price, String brand, int era, ControlType controlType) {
        super(name, stock, price, brand, era);
        this.controlType = controlType;
    }
}