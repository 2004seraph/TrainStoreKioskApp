package entity.order;

import db.DatabaseOperation;
import db.DatabaseRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Order extends DatabaseOperation.Entity implements DatabaseRecord {
    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String msg) {
            super(msg);
        }
    }
    private Integer orderId = -1;
    private Integer customerId;
    private Date date;

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        FULFILLED
    }
    private OrderStatus status;

    private List<OrderLine> items = new ArrayList<OrderLine>();

    public Order(Integer customerId) {
        this.customerId = customerId;
        this.date = new Date();
        this.status = OrderStatus.PENDING;
    }

    public Order(Integer customerId, Date date, OrderStatus status) {
        this.customerId = customerId;
        this.date = date;
        this.status = status;
    }

    public Order(Integer orderId, Integer customerId, Date date, OrderStatus status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.date = date;
        this.status = status;
    }

    public Order(Integer orderId, Integer customerId, Date date, OrderStatus status, List<OrderLine> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.date = date;
        this.status = status;
        this.items = items;
    }


    public List<Object> GetFields() {
        List<Object> list = Arrays.asList(
                customerId,
                date.toString(),
                status.toString()
        );
        return list;
    }
}
