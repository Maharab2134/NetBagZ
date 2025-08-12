package com.example.net.Model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String title;
    private String price;
    private int imageResId;
    private int quantity;
    private String size; // Add size field

    public CartItem(String title, String price, int imageResId, String size, int quantity) {
        this.title = title;
        this.price = price;
        this.imageResId = imageResId;
        this.size = size;
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
