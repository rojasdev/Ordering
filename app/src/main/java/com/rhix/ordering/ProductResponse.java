package com.rhix.ordering;

import java.util.List;

public class ProductResponse {
    private String message;
    private List<Product> products;

    public String getMessage() {
        return message;
    }

    public List<Product> getProducts() {
        return products;
    }
}
