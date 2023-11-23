package utils;

import java.text.NumberFormat;
import java.util.Locale;

public final class GUI {
    private GUI() {}

    public static final NumberFormat ukCurrencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "GB"));
}
