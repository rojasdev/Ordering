package com.rhix.ordering;

public class OrderItem {
    private int id;
    private String customer_name;
    private String order_details;
    private double total_price;
    private String order_date;

    public OrderItem(int id, String customer_name, String order_details, double total_price, String order_date) {
        this.id = id;
        this.customer_name = customer_name;
        this.order_details = order_details;
        this.total_price = total_price;
        this.order_date = order_date;
    }
    // Getters for each field
    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customer_name;
    }

    public String getOrderDetails() {
        return order_details;
    }

    public double getTotalPrice() {
        return total_price;
    }



    public String getOrderDate() {
        return order_date;
    }
}
