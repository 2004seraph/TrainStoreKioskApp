package entity.product;

public class Locomotive extends Component{
    private PriceBracket priceBracket;

    public Locomotive(String name, int stock, Double price, String brand, int era, PriceBracket priceBracket) {
        super(name, stock, price, brand, era);
        this.priceBracket = priceBracket;
    }

    public enum PriceBracket {
        ANALOGUE,
        DDC_READY,
        DCC_SOUND,
        DDC_FITTED
    }
}