package entity.product;

public class BoxedSet extends Product{
    protected Component[] component;

    public BoxedSet(String name, int stock, Double price) {
        super(name, stock, price);
    }
}