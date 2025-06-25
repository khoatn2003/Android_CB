package com.example.app_fast_food.Model;

import com.example.app_fast_food.Cart.CartItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    private int userId; // ID của người dùng sở hữu giỏ hàng này
    private List<CartItem> items;

    public Cart(int userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    public Cart(int userId, List<CartItem> items) {
        this.userId = userId;
        this.items = items != null ? items : new ArrayList<>();
    }

    public int getUserId() {
        return userId;
    }

    public List<CartItem> getItems() {
        return items;
    }
 
    // Phương thức tiện ích
    public void addItem(CartItem item) {
        for (CartItem existingItem : items) {
            if (existingItem.getFoodId() == item.getFoodId() && ((existingItem.getNote() == null && item.getNote() == null)
                    || (existingItem.getNote() != null && existingItem.getNote().equals(item.getNote())))) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        // Nếu không trùng cả ID và Note thì thêm mới
        items.add(item);
    }

    public CartItem getItemByFoodId(int foodId) {
        for (CartItem item : items) {
            if (item.getFoodId() == foodId) {
                return item;
            }
        }
        return null;
    }
}
