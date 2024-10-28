package com.rhix.ordering;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private String image_url;

    public Product(int id, String name, String description, double price, String image_url) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_url = image_url;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageUrl() { return image_url; }
}
