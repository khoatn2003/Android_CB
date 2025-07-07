package com.example.app_fast_food.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.app_fast_food.Cart.CartItem;
import com.example.app_fast_food.Model.Cart;
import com.example.app_fast_food.Model.Foods;

public class CartDatabase {
    private SQLiteDatabase db;

    public CartDatabase(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    // Thêm vào giỏ
    public boolean addToCart(int userId, Foods food, int quantityToAdd, String note) {
        if (userId <= 0 || food == null || quantityToAdd <= 0) {
            Log.w("Cart", "Tham số không hợp lệ hoặc số lượng <= 0");
            return false;
        }

        String safeNote = (note == null) ? "" : note.trim();
        Cursor cursor = null;

        try {
            cursor = db.query(DatabaseHelper.TABLE_CART,
                    new String[]{DatabaseHelper.COLUMN_CART_ID, DatabaseHelper.COLUMN_CART_QUANTITY},
                    DatabaseHelper.COLUMN_CART_USER_ID + "=? AND " +
                            DatabaseHelper.COLUMN_CART_FOOD_ID + "=? AND " +
                            DatabaseHelper.COLUMN_CART_NOTE + "=?",
                    new String[]{String.valueOf(userId), String.valueOf(food.getId()), safeNote},
                    null, null, null);

            if (cursor.moveToFirst()) {
                int cartId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_ID));
                int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_QUANTITY));
                int newQuantity = currentQuantity + quantityToAdd;

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_CART_QUANTITY, newQuantity);

                int updated = db.update(DatabaseHelper.TABLE_CART, values,
                        DatabaseHelper.COLUMN_CART_ID + "=?",
                        new String[]{String.valueOf(cartId)});
                Log.i("Cart", "Cập nhật số lượng sản phẩm: " + updated + " dòng");
                return updated > 0;
            } else {
                // Thêm sản phẩm mới vào giỏ
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_CART_USER_ID, userId);
                values.put(DatabaseHelper.COLUMN_CART_FOOD_ID, food.getId());
                values.put(DatabaseHelper.COLUMN_CART_FOOD_TITLE, food.getTitle());
                values.put(DatabaseHelper.COLUMN_CART_FOOD_IMAGE_PATH, food.getImagePath());
                values.put(DatabaseHelper.COLUMN_CART_QUANTITY, quantityToAdd);
                values.put(DatabaseHelper.COLUMN_CART_PRICE_PER_ITEM, food.getPrice());
                values.put(DatabaseHelper.COLUMN_CART_NOTE, safeNote);

                long inserted = db.insert(DatabaseHelper.TABLE_CART, null, values);
                Log.i("Cart", "Thêm mới sản phẩm vào giỏ: " + inserted);
                return inserted != -1;
            }

        } catch (Exception e) {
            Log.e("Cart", "Lỗi khi thêm/cập nhật giỏ hàng", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }


    public boolean updateToCartById(int cartId, int userId, int foodId, int currentQuantity, int newQuantityInput, String newNote) {
        if (cartId <= 0) {
            Log.e("Cart", "Invalid cartId");
            return false;
        }

        String safeNewNote = (newNote == null) ? "" : newNote.trim();

        Cursor cursor = null;
        try {
            if (newQuantityInput <= 0) {
                int deleteResult = db.delete(DatabaseHelper.TABLE_CART,
                        DatabaseHelper.COLUMN_CART_ID + "=?",
                        new String[]{String.valueOf(cartId)});
                Log.i("Cart", "Xóa sản phẩm khỏi giỏ hàng. Số dòng bị xóa: " + deleteResult);
                return deleteResult > 0;
            }

            // Nếu không thay đổi gì -> bỏ qua
            if (currentQuantity == newQuantityInput) {
                Cursor oldCursor = db.query(DatabaseHelper.TABLE_CART,
                        new String[]{DatabaseHelper.COLUMN_CART_NOTE
                        },
                        DatabaseHelper.COLUMN_CART_ID + "=?",
                        new String[]{String.valueOf(cartId)},
                        null, null, null);

                if (oldCursor != null && oldCursor.moveToFirst()) {
                    String oldNote = oldCursor.getString(oldCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_NOTE));
                    oldCursor.close();

                    if (safeNewNote.equals(oldNote)) {
                        Log.i("Cart", "Không có thay đổi gì → không cập nhật");
                        return true;
                    }
                }
            }

            // Kiểm tra xem có dòng khác cùng userId + foodId + newNote chưa
            cursor = db.query(DatabaseHelper.TABLE_CART,
                    new String[]{DatabaseHelper.COLUMN_CART_ID, DatabaseHelper.COLUMN_CART_QUANTITY},
                    DatabaseHelper.COLUMN_CART_USER_ID + "=? AND " +
                            DatabaseHelper.COLUMN_CART_FOOD_ID + "=? AND " +
                            DatabaseHelper.COLUMN_CART_NOTE + "=? AND " +
                            DatabaseHelper.COLUMN_CART_ID + "!=?", // Tránh chính mình
                    new String[]{
                            String.valueOf(userId),
                            String.valueOf(foodId),
                            safeNewNote,
                            String.valueOf(cartId)
                    },
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Đã có dòng khác trùng note → gộp
                int targetCartId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_ID));
                int existingQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_QUANTITY));
                int totalQuantity = existingQuantity + newQuantityInput;

                ContentValues mergeValues = new ContentValues();
                mergeValues.put(DatabaseHelper.COLUMN_CART_QUANTITY, totalQuantity);

                int updateResult = db.update(DatabaseHelper.TABLE_CART, mergeValues,
                        DatabaseHelper.COLUMN_CART_ID + "=?",
                        new String[]{String.valueOf(targetCartId)});

                // Xóa dòng cũ
                db.delete(DatabaseHelper.TABLE_CART,
                        DatabaseHelper.COLUMN_CART_ID + "=?",
                        new String[]{String.valueOf(cartId)});

                Log.i("Cart", "Gộp dòng ghi chú trùng. Update dòng của id cart: " + targetCartId + ", xóa dòng của cart id: " + cartId);
                return updateResult > 0 ;
            } else {
                // Không có dòng trùng → cập nhật bình thường
                ContentValues updateValues = new ContentValues();
                updateValues.put(DatabaseHelper.COLUMN_CART_QUANTITY, newQuantityInput);
                updateValues.put(DatabaseHelper.COLUMN_CART_NOTE, safeNewNote);

                int updateResult = db.update(DatabaseHelper.TABLE_CART, updateValues,
                        DatabaseHelper.COLUMN_CART_ID + "=?",
                        new String[]{String.valueOf(cartId)});

                Log.i("Cart", "Đã cập nhật sản phẩm trong giỏ: " + "từ số lượng" + currentQuantity + "thành số lượng" + newQuantityInput + " số lượng");

                return updateResult > 0;
            }

        } catch (Exception e) {
            Log.e("Cart", "Lỗi khi cập nhật giỏ hàng", e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    // Lấy giỏ theo userId
    public Cart getCartByUser(int userId) {
        Cart cart = new Cart(userId);
        Cursor cursor = null;
        try {
           cursor = db.query(
                    DatabaseHelper.TABLE_CART,
                    null,
                    DatabaseHelper.COLUMN_CART_USER_ID + "=?",
                    new String[]{String.valueOf(userId)},
                    null, null, DatabaseHelper.COLUMN_CART_ID + " ASC"
            );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int cartId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_ID));
                int foodId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_FOOD_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_FOOD_TITLE));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_FOOD_IMAGE_PATH));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_QUANTITY));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_PRICE_PER_ITEM));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_NOTE));
                cart.addItem(new CartItem(cartId,foodId, title, imagePath, quantity, price, note));
                Log.d("CartDatabase", "Lấy sản phẩm: " + title + ", số lượng: " + quantity);
            } while (cursor.moveToNext());
        }
        }catch (Exception e){
            Log.e("DatabaseHelper", "Error while trying to get foods by category from database", e);
        } finally {
            if (cursor != null) {
                cursor.close(); // 3. Luôn đóng Cursor
            }
        }
        return cart;
    }

    public int getQuantityInCart(int userId, int foodId, String note) {
        String safeNote = (note == null) ? "" : note.trim();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CART,
                new String[]{DatabaseHelper.COLUMN_CART_QUANTITY},
                DatabaseHelper.COLUMN_CART_USER_ID + "=? AND " +
                        DatabaseHelper.COLUMN_CART_FOOD_ID + "=? AND " +
                        DatabaseHelper.COLUMN_CART_NOTE + "=?",
                new String[]{String.valueOf(userId), String.valueOf(foodId), safeNote},
                null, null, null);

        int quantity = 0;
        if (cursor != null && cursor.moveToFirst()) {
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_QUANTITY));
            cursor.close();
        }
        return quantity;
    }


    // Cập nhật số lượng của một sản phẩm cụ thể trong giỏ hàng của người dùng
    public boolean updateCartItemQuantity(int userId, int foodId, int newQuantity, String note) {
        if (userId <= 0) return false;
        String safeNote = (note == null) ? "" : note.trim();
        ContentValues values = new ContentValues();

        if (newQuantity <= 0) { // Nếu số lượng mới <= 0, xóa sản phẩm
            return removeCartItem(userId, foodId, safeNote);
        } else {
            values.put(DatabaseHelper.COLUMN_CART_QUANTITY, newQuantity);
            int rowsAffected = db.update(DatabaseHelper.TABLE_CART, values,
                    DatabaseHelper.COLUMN_CART_USER_ID + "=? AND "
                            + DatabaseHelper.COLUMN_CART_FOOD_ID + "=? AND "
                            + DatabaseHelper.COLUMN_CART_NOTE + "=?",
                    new String[]{String.valueOf(userId), String.valueOf(foodId), safeNote});
            // db.close();
            return rowsAffected > 0;
        }
    }

    // Xóa một sản phẩm cụ thể khỏi giỏ hàng của người dùng
    public boolean removeCartItem(int userId, int foodId, String note) {
        if (userId <= 0) return false;

        String safeNote = (note == null) ? "" : note.trim();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_CART,
                DatabaseHelper.COLUMN_CART_USER_ID + "=? AND "
                        + DatabaseHelper.COLUMN_CART_FOOD_ID + "=? AND "
                        + DatabaseHelper.COLUMN_CART_NOTE + "=?",
                new String[]{String.valueOf(userId),
                        String.valueOf(foodId),
                        safeNote
             });
        // db.close();
        return rowsAffected > 0;
    }
    // Xóa toàn bộ giỏ
    public void clearCart(int userId) {
        db.delete(DatabaseHelper.TABLE_CART,
                DatabaseHelper.COLUMN_CART_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
    }
}

