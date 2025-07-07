package com.example.app_fast_food.Order;

import com.example.app_fast_food.Cart.CartItem;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private int userId;
    private double totalPrice;
    private String orderDate;
    private String receiverName;
    private String shippingAddress;
    private String phone;
    private List<OrderItem> items;

    // Constructors, getters, setters

    public Order() {
    }

    public Order(int userId, double totalPrice, String orderDate, String receiverName, String shippingAddress, String phone, List<OrderItem> items) {
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.receiverName = receiverName;
        this.shippingAddress = shippingAddress;
        this.phone = phone;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}


