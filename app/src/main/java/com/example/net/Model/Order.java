package com.example.net.Model;

public class Order {
    private String userName;
    private String productTitle;
    private String totalAmount;
    private String paymentMethod;
    private String orderStatus;
    private long orderTime;

    public Order(String userName, String productTitle, String totalAmount, String paymentMethod, String orderStatus, long orderTime) {
        this.userName = userName;
        this.productTitle = productTitle;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
    public long getOrderTime() { return orderTime; }
}
