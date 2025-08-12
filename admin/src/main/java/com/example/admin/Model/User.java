package com.example.admin.Model;

public class User {
    private String id;
    private String name;
    private String phone;
    private String shopname;
    private String address;
    private String status;
    private String postalCode;


    public User(String id, String name, String phone, String shopname, String address, String status, String postalCode) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.shopname = shopname;
        this.address = address;
        this.status = status;
        this.postalCode = postalCode;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getShopname() {
        return shopname;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getPostalCode() {
        return postalCode;
    }
}
