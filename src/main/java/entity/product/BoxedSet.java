package entity.product;

import org.javatuples.Pair;

import java.sql.SQLException;
import java.util.List;

public class BoxedSet extends Product{
    // Each Pair contains the component and the quantity
    protected List<Pair<Component, Integer>> components;
    protected List<Pair<BoxedSet, Integer>> boxedSets;

    public BoxedSet(String name, int stock, Double price, List<Pair<Component, Integer>> components, List<Pair<BoxedSet, Integer>> boxedSets) {
        super(name, stock, price);
        this.components = components;
        this.boxedSets = boxedSets;
    }

    public List<Pair<Component, Integer>> getComponents() {
        return components;
    }

    public List<Pair<BoxedSet, Integer>> getBoxedSets() {
        return this.boxedSets;
    }
}