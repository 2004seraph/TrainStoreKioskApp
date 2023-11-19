package gui;

import javax.swing.*;
import java.util.Map;

public class ShopCard {
    private JPanel cardBody;
    private JTextField quantityTextField;
    private JButton addToCartButton;
    private JButton showMoreButton;
    private JLabel priceLabel;
    private JLabel priceValue;
    private JLabel productNameLabel;
    private JLabel quantityLabel;
    private JLabel attributeLabel;
    private JLabel attributeValue;
    private JLabel brandLabel;
    private JLabel eraLabel;
    private JLabel gaugeLabel;
    private JLabel priceBracketLabel;
    private JLabel controlTypeLabel;

    public ShopCard(Map<String, Object> productInfo) {
        String productName = (String) productInfo.get("productName");
        double price = (double) productInfo.get("price");

        productNameLabel.setText(productName);
        priceValue.setText(String.valueOf(price));

        String values = "";

        for (Map.Entry<String, Object> entry : productInfo.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Check if the key is one of the additional properties (brand, era, gauge)
            if ("brand".equals(key) || "era".equals(key) || "gauge".equals(key)|| "priceBracket".equals(key) || "controlType".equals(key)) {
                switch (key) {
                    case "brand" -> values += "Brand: " + value + " ";
                    case "era" -> eraLabel.setText(String.valueOf(value));
                    case "gauge" -> gaugeLabel.setText(String.valueOf(value));
                    case "priceBracket" -> priceBracketLabel.setText(String.valueOf(value));
                    case "controlType" -> controlTypeLabel.setText(String.valueOf(value));
                }
            }
        }
    }

}
