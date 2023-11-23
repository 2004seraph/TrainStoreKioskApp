package entity.product;

public class Locomotive extends Component{
    public enum PriceBracket {
        ANALOGUE,
        DCC_READY,
        DCC_SOUND,
        DCC_FITTED;

        // Put this here because the _s make valueOf trip out
        public static PriceBracket get(String value) {
            return switch (value) {
                case "ANALOGUE" -> PriceBracket.ANALOGUE;
                case "DCC_READY" -> PriceBracket.DCC_READY;
                case "DCC_SOUND" -> PriceBracket.DCC_SOUND;
                case "DCC_FITTED" -> PriceBracket.DCC_FITTED;
                default -> throw new IllegalStateException("Unexpected value: " + value);
            };
        }
    }

    private PriceBracket priceBracket;

    public Locomotive(String name, int stock, Double price, String brand, int era, Gauge gauge, PriceBracket priceBracket) {
        super(name, stock, price, brand, era, gauge);
        this.priceBracket = priceBracket;
    }

    public PriceBracket getPriceBracket() {
        return priceBracket;
    }
}