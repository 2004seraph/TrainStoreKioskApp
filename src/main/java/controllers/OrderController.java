package controllers;

import entity.order.Order;

public final class OrderController {
    public static Order currentOrder;

    public static Order getCurrentOrder() {
        return currentOrder;
    }

    public static void newOrder() {
        currentOrder = new Order(AppContext.getCurrentUser().getId());
    }

    public static boolean checkout() {
        //TODO: Pay for order

        newOrder();
        return false;
    }
}
