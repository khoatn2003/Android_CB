package com.example.app_fast_food.Order;

import java.io.Serializable;

public class OrderItemDisplay implements Serializable {
    private String orderID;
    private int orderItemID;
    private int foodID;
    private String foodTitle;
    private String imagePath;
    private int quantity;
    private double pricePerItem;
    private String note;
    private String status;

    private String orderDateItem;

    private String orderShippingTime;
    private String orderReceiveTime;
    private String orderCancelTime;
    private String addressOrder;

    private String paymentMethod;
    // Getters/setters...

    public OrderItemDisplay() {
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public int getOrderItemID() {
        return orderItemID;
    }

    public void setOrderItemID(int orderItemID) {
        this.orderItemID = orderItemID;
    }

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public String getFoodTitle() {
        return foodTitle;
    }

    public void setFoodTitle(String foodTitle) {
        this.foodTitle = foodTitle;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDateItem() {
        return orderDateItem;
    }

    public void setOrderDateItem(String orderDateItem) {
        this.orderDateItem = orderDateItem;
    }

    public String getAddressOrder() {
        return addressOrder;
    }

    public void setAddressOrder(String addressOrder) {
        this.addressOrder = addressOrder;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderShippingTime() {
        return orderShippingTime;
    }

    public void setOrderShippingTime(String orderShippingTime) {
        this.orderShippingTime = orderShippingTime;
    }

    public String getOrderReceiveTime() {
        return orderReceiveTime;
    }

    public void setOrderReceiveTime(String orderReceiveTime) {
        this.orderReceiveTime = orderReceiveTime;
    }

    public String getOrderCancelTime() {
        return orderCancelTime;
    }

    public void setOrderCancelTime(String orderCancelTime) {
        this.orderCancelTime = orderCancelTime;
    }
}
