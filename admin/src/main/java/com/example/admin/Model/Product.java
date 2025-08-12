package com.example.admin.Model;

public class Product {

    private String id;
    private String name;
    private String price;
    private String details;
    private String description;
    private String rating;

    // Constructor
    public Product(String id, String name, String price, String details, String description, String rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.details = details;
        this.description = description;
        this.rating = rating;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
