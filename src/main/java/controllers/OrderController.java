package controllers;

import db.DatabaseBridge;
import entity.BankDetail;
import entity.order.Order;
import entity.user.Person;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public final class OrderController {
    public static Order currentOrder;

    public static void newOrder() {
        currentOrder = new Order(AppContext.getCurrentUser().getId());
    }

    public static boolean checkout() {
        Person user = AppContext.getCurrentUser();
        BankDetail bankDetail = user.getBankDetail();

        if (bankDetail == null) {
            System.out.println("null");
            return false;
        }

        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            BankDetail.validateBankDetails(bankDetail.getCardName(), bankDetail.getCardNumber(), bankDetail.getCardHolderName(), df.format(bankDetail.getExpiryDate()), bankDetail.getSecurityCode());
            // error
        } catch (BankDetail.InvalidBankDetailsException e) {
            System.out.println("bad validation");
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
