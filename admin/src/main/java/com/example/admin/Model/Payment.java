package com.example.admin.Model;

public class Payment {

    private String id;
    private String userName;
    private String userShopName;
    private String totalAmount;
    private String paymentMethod;
    private String transactionId;
    private String invoiceId;
    private String status;

    // Constructor
    public Payment(String id, String userName, String userShopName, String totalAmount,
                   String paymentMethod, String transactionId, String invoiceId, String status) {
        this.id = id;
        this.userName = userName;
        this.userShopName = userShopName;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.invoiceId = invoiceId;
        this.status = status;
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserShopName() {
        return userShopName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getStatus() {
        return status;
    }

    // Setter methods
    public void setId(String id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserShopName(String userShopName) {
        this.userShopName = userShopName;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
