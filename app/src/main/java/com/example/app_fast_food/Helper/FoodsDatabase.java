package com.example.app_fast_food.Helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.app_fast_food.Model.Foods;

import java.util.ArrayList;
import java.util.List;

public class FoodsDatabase {
    private final SQLiteDatabase db;

    public FoodsDatabase(SQLiteDatabase db) {
        this.db = db;
    }

    public boolean addFood(Foods food) { // Sử dụng model Foods bạn đã tạo
        if (food == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FOOD_ID, food.getId());
        values.put(DatabaseHelper.COLUMN_FOOD_TITLE, food.getTitle());
        values.put(DatabaseHelper.COLUMN_FOOD_DESCRIPTION, food.getDescription());
        values.put(DatabaseHelper.COLUMN_FOOD_PRICE, food.getPrice());
        values.put(DatabaseHelper.COLUMN_FOOD_IMAGE_PATH, food.getImagePath()); // Đảm bảo tên cột này đúng
        values.put(DatabaseHelper.COLUMN_FOOD_FK_CATEGORY_ID, food.getCategoryId());
        values.put(DatabaseHelper.COLUMN_FOOD_STAR, food.getStar());
        values.put(DatabaseHelper.COLUMN_FOOD_TIME_VALUE, food.getTimeValue());
        values.put(DatabaseHelper.COLUMN_BEST_FOOD, food.isBestFood() ? 1 : 0); // Chuyển boolean sang int

        long result = db.insert(DatabaseHelper.TABLE_FOODS, null, values);
        return result != -1;
    }
    public List<Foods> getAllFoods() {
        List<Foods> listFood = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_FOODS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_DESCRIPTION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_PRICE));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_IMAGE_PATH));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_FK_CATEGORY_ID));
                double star = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_STAR));
                int timeValue = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_TIME_VALUE));
                boolean bestFood = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BEST_FOOD)) == 1;
                listFood.add(new Foods(id, title,description,price,imagePath,categoryId,star,timeValue,bestFood));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return listFood;
    }

    public List<Foods> getBestFoods() {
        List<Foods> bestFoodList = new ArrayList<>();

        Cursor cursor = db.query(DatabaseHelper.TABLE_FOODS,
                null,
                DatabaseHelper.COLUMN_BEST_FOOD + " = ?",
                new String[]{"1"},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_DESCRIPTION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_PRICE));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_IMAGE_PATH));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_FK_CATEGORY_ID));
                double star = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_STAR));
                int timeValue = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_TIME_VALUE));
                boolean bestFood = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BEST_FOOD)) == 1;

                Foods food = new Foods(id, title, description, price, imagePath, categoryId, star, timeValue, bestFood);
                bestFoodList.add(food);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return bestFoodList;
    }

    public List<Foods> getFoodsByCategoryId(int categoryId) {
        List<Foods> foodList = new ArrayList<>(); // 1. Tạo một List để chứa kết quả
        Cursor cursor = null;

        String selection = DatabaseHelper.COLUMN_FOOD_FK_CATEGORY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(categoryId)};

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_FOODS,
                    null, // Lấy tất cả các cột
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            // 2. Duyệt qua Cursor và chuyển đổi mỗi dòng thành một đối tượng Foods
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Lấy dữ liệu từ Cursor
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_DESCRIPTION));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_PRICE));
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_IMAGE_PATH));
                    // int catId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_FK_CATEGORY_ID)); // Đã có categoryId từ tham số
                    double star = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_STAR));
                    int timeValue = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_TIME_VALUE));
                    boolean bestFood = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BEST_FOOD))==1;

                    // Tạo đối tượng Foods
                    Foods food = new Foods(id, title, description, price, imagePath, categoryId, star, timeValue, bestFood);
                    foodList.add(food); // Thêm vào List
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while trying to get foods by category from database", e);
        } finally {
            if (cursor != null) {
                cursor.close(); // 3. Luôn đóng Cursor
            }
            // Không đóng db ở đây nếu helper còn được dùng ở nhiều nơi
        }
        return foodList; // 4. Trả về List các đối tượng Foods
    }
    public Foods getFoodById(int foodId) {
        Foods food = null;
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_FOODS,
                    null,
                    DatabaseHelper.COLUMN_FOOD_ID + "=?",
                    new String[]{String.valueOf(foodId)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                food = new Foods();
                food.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_ID)));
                food.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_TITLE)));
                food.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_DESCRIPTION)));
                food.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_PRICE)));
                food.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_IMAGE_PATH)));
                food.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_FK_CATEGORY_ID)));
                food.setStar(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_STAR)));
                food.setTimeValue(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_TIME_VALUE)));
                food.setBestFood(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BEST_FOOD)) == 1);
            }
        } catch (Exception e) {
            Log.e("DB", "Lỗi lấy food theo ID", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return food;
    }
    public List<Foods> getSuggestedFoods() {
        List<Foods> result = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_FOODS + " ORDER BY " + DatabaseHelper.COLUMN_FOOD_STAR + " DESC LIMIT 10", null);
        if (cursor.moveToFirst()) {
            do {
                Foods f = new Foods();
                f.setId(cursor.getInt(0));
                f.setTitle(cursor.getString(1));
                f.setDescription(cursor.getString(2));
                f.setPrice(cursor.getDouble(3));
                f.setImagePath(cursor.getString(4));
                f.setCategoryId(cursor.getInt(5));
                f.setStar(cursor.getDouble(6));
                f.setTimeValue(cursor.getInt(7));
                result.add(f);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }
}
