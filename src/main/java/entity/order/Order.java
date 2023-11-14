package entity.order;

import db.DatabaseOperation;
import db.DatabaseRecord;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

public class Order extends DatabaseOperation.Entity implements DatabaseRecord {
    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String msg) {
            super(msg);
        }
    }
    private Integer orderId;
    private Integer customerId;
    private Date date;

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        FULFILLED
    }
    private OrderStatus status;

    public List<Object> GetFields() {
        List<Object> list = Arrays.asList(
                customerId,
                date,
                status.toString()
        );
        return list;
    }
}
