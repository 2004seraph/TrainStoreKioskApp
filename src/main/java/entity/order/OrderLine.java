package entity.order;

import db.DatabaseOperation;
import db.DatabaseRecord;
import entity.product.Product;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class OrderLine extends DatabaseOperation.Entity implements DatabaseRecord {
    private Integer orderId;
    private String productCode;
    private Integer quantity;

    private Product item;

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Product getItem() throws SQLException {
        if (item != null) {
            return item;
        }
        return Product.getProductByID(productCode);
    }

    public void setItem(Product item) {
        this.item = item;
    }

    public String getProductCode() {
        return productCode;
    }

    public Integer getQuantity() {
        return this.quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OrderLine(Integer orderId, String productCode, Integer quantity) {
        this.orderId = orderId;
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public List<Object> getFields() {
        List<Object> list = Arrays.asList(
                orderId,
                productCode,
                quantity
        );
        return list;
    }
}
