package com.rhix.ordering;

import java.util.List;

public class OrderListResponse {
    private List<OrderItem> orders;
    private String message;

    public List<OrderItem> getOrders() {
        return orders;
    }

    public String getMessage() {
        return message;
    }
}
