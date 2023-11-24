package controllers;

import db.DatabaseBridge;
import entity.BankDetail;
import entity.order.Order;
import entity.user.Person;

import java.sql.SQLException;

public final class OrderController {
    public static Order currentOrder;

    public static void newOrder() {
        currentOrder = new Order(AppContext.getCurrentUser().getId());
    }

    public static boolean checkout() {
        Person user = AppContext.getCurrentUser();
        BankDetail bankDetail = user.getBankDetail();

        if (bankDetail == null) {
            return false;
        }

        try {
            BankDetail.validateBankDetails(bankDetail.getCardNumber(), bankDetail.getExpiryDate().toString(), bankDetail.getSecurityCode());
        } catch (BankDetail.InvalidBankDetailsException e) {
            return false;
        }

        try {
            currentOrder.setStatus(Order.OrderStatus.CONFIRMED);
            Order.createOrder(currentOrder);
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to create new order", e);
            throw new RuntimeException(e);
        }

        newOrder();
        return true;
    }
}
