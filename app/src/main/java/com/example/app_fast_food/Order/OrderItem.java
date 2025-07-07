package com.example.app_fast_food.Order;

import com.example.app_fast_food.Cart.CartItem;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private int orderItemId;
    private String orderId;
    private int foodId;
    private int quantity;
    private double pricePerItem;
    private String note;
    private String status;
    private String deliveryTime;
    private String receiveTime;
    private String cancelTime;

    public OrderItem() {
    }

    public OrderItem(int orderItemId, String orderId, int foodId, int quantity, double pricePerItem, String note, String status) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.note = note;
        this.status = status;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getFoodId() {
        return foodId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerItem() {
        return pricePerItem;
    }

    public String getNote() {
        return note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }

    public static OrderItem fromCartItem(CartItem cartItem, String orderId, String status, String deliveryTime, String receiveTime) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setFoodId(cartItem.getFoodId());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPricePerItem(cartItem.getPricePerItem());
        orderItem.setNote(cartItem.getNote());
        orderItem.setStatus(status);
        orderItem.setDeliveryTime(deliveryTime);
        orderItem.setReceiveTime(receiveTime);
        return orderItem;
    }


}

