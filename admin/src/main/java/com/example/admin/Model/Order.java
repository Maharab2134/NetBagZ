package com.example.admin.Model;

public class Order {
    private String userName;
    private String shopName;
    private String invoiceId;
    private String totalAmount;
    private String paymentMethod;
    private String productSize;
    private String address;
    private int productQuantity;
    private String productTitle;
    private String status;  // Add status field

    // Constructor
    public Order(String userName, String shopName, String invoiceId, String totalAmount, String paymentMethod, String productSize,
                 int productQuantity, String productTitle, String address, String status) {
        this.userName = userName;
        this.shopName = shopName;
        this.invoiceId = invoiceId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.productSize = productSize;
        this.productQuantity = productQuantity;
        this.productTitle = productTitle;
        this.address = address;
        this.status = status;
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public String getShopName() {
        return shopName;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }


    public int getProductQuantity() {
        return productQuantity;
    }
    public String getAddress() {
        return address;
    }


    public String getProductTitle() {
        return productTitle;
    }
}
