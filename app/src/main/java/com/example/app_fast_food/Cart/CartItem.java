package com.example.app_fast_food.Cart;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int cartId;
    private int foodId;
    private String foodTitle;
    private String foodImagePath;
    private int quantity;
    private double pricePerItem;
    private String note;

    public CartItem() {
    }

    public CartItem(int cartId,int foodId, String foodTitle, String foodImagePath, int quantity, double pricePerItem, String note) {
        this.cartId = cartId;
        this.foodId = foodId;
        this.foodTitle = foodTitle;
        this.foodImagePath = foodImagePath;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.note = note;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodTitle() {
        return foodTitle;
    }

    public void setFoodTitle(String foodTitle) {
        this.foodTitle = foodTitle;
    }

    public String getFoodImagePath() {
        return foodImagePath;
    }

    public void setFoodImagePath(String foodImagePath) {
        this.foodImagePath = foodImagePath;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public double getToTalPrice(){return quantity*pricePerItem;}
}
