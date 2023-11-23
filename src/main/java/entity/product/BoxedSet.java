package entity.product;

import org.javatuples.Pair;
import java.util.List;

public class BoxedSet extends Product{
    // Each Pair contains the component and the quantity
    protected List<Pair<Component, Integer>> components;

    public BoxedSet(String name, int stock, Double price, List<Pair<Component, Integer>> components) {
        super(name, stock, price);
        this.components = components;
    }
}