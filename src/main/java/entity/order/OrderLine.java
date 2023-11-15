package entity.order;

import db.DatabaseOperation;
import db.DatabaseRecord;

import java.util.Arrays;
import java.util.List;

public class OrderLine extends DatabaseOperation.Entity implements DatabaseRecord {
    private Integer orderId;
    private String productCode;
    private Integer quantity;

    public OrderLine(Integer orderId, String productCode, Integer quantity) {
        this.orderId = orderId;
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public List<Object> GetFields() {
        List<Object> list = Arrays.asList(
                orderId,
                productCode,
                quantity
        );
        return list;
    }
}
