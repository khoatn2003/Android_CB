package com.example.app_fast_food.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.FileUriExposedException;
import android.util.Log;

import com.example.app_fast_food.Cart.CartItem;
import com.example.app_fast_food.Model.Category;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.Model.Users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME ="FoodDB.db";
    private static final int DATABASE_VERSION = 7;

    //Bảng user
    private static final String TABLE_USERS = "Users";
    private static final String COLUMN_USER_ID = "userID";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_FULL_NAME = "full_name";


    //Bảng Category
    private static final String TABLE_CATEGORY = "Category";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_IMAGE_PATH = "ImagePath";
    private static final String COLUMN_NAME = "Name";


    //Bảng Foods
    public static final String TABLE_FOODS = "Foods";
    public static final String COLUMN_FOOD_ID = "food_id";
    public static final String COLUMN_FOOD_TITLE = "title";
    public static final String COLUMN_FOOD_DESCRIPTION = "description";
    public static final String COLUMN_FOOD_PRICE = "price";
    public static final String COLUMN_FOOD_IMAGE_PATH = "image_path";
    public static final String COLUMN_FOOD_FK_CATEGORY_ID = "category_id";
    public static final String COLUMN_FOOD_STAR= "Star";
    public static final String COLUMN_FOOD_TIME_VALUE = "TimeValue";
    public static final String COLUMN_BEST_FOOD = "BestFood";

    //Bảng Giỏ hàng
    public static final String TABLE_CART = "Cart";
    public static final String COLUMN_CART_ID = "Cart_Id"; // Khóa chính của bảng cart, có thể tự tăng
    public static final String COLUMN_CART_USER_ID = "User_Id";
    public static final String COLUMN_CART_FOOD_ID = "Food_id"; // Khóa ngoại tham chiếu đến Foods.id
    public static final String COLUMN_CART_FOOD_TITLE = "Food_Title"; // Lưu lại title cho tiện
    public static final String COLUMN_CART_FOOD_IMAGE_PATH = "Food_Image_Path"; // Lưu lại image path
    public static final String COLUMN_CART_QUANTITY = "Quantity";
    public static final String COLUMN_CART_PRICE_PER_ITEM = "Price_Per_Item"; // Giá của 1 sản phẩm tại thời điểm thêm
    public static final String COLUMN_CART_NOTE = "Note";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_EMAIL + " TEXT ,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_FULL_NAME + " TEXT)";
        db.execSQL(CREATE_TABLE);
        Log.i("DatabaseHelper", TABLE_USERS + " table created with version " + DATABASE_VERSION);
        // Thêm bảng Category chưa có
        String CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_IMAGE_PATH + " TEXT,"
                + COLUMN_NAME + " TEXT)";
        db.execSQL(CREATE_CATEGORY_TABLE);

        // Kiểm tra xem bảng Foods đã tồn tại chưa (phòng trường hợp onUpgrade phức tạp)
        String CREATE_FOODS_TABLE_UPGRADE = "CREATE TABLE IF NOT EXISTS " + TABLE_FOODS + " ("
                + COLUMN_FOOD_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_FOOD_TITLE + " TEXT NOT NULL,"
                + COLUMN_FOOD_DESCRIPTION + " TEXT,"
                + COLUMN_FOOD_PRICE + " REAL NOT NULL,"
                + COLUMN_FOOD_IMAGE_PATH + " TEXT,"
                + COLUMN_FOOD_FK_CATEGORY_ID + " INTEGER,"
                + COLUMN_FOOD_STAR + " REAL DEFAULT 4.0,"
                + COLUMN_FOOD_TIME_VALUE + " INTEGER,"
                + COLUMN_BEST_FOOD + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY (" + COLUMN_FOOD_FK_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_ID + ")"
                + ");";
        db.execSQL(CREATE_FOODS_TABLE_UPGRADE);

        // Thêm tạo bảng Cart ngay trong onCreate (dành cho cài mới)
        String CREATE_CART_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CART + "("
                + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CART_USER_ID + " INTEGER NOT NULL,"
                + COLUMN_CART_FOOD_ID + " INTEGER NOT NULL,"
                + COLUMN_CART_FOOD_TITLE + " TEXT,"
                + COLUMN_CART_FOOD_IMAGE_PATH + " TEXT,"
                + COLUMN_CART_QUANTITY + " INTEGER NOT NULL,"
                + COLUMN_CART_PRICE_PER_ITEM + " REAL NOT NULL,"
                + COLUMN_CART_NOTE + " TEXT,"
                + "UNIQUE (" + COLUMN_CART_USER_ID + ", " + COLUMN_CART_FOOD_ID + ", " + COLUMN_CART_NOTE + "),"
                + "FOREIGN KEY (" + COLUMN_CART_FOOD_ID + ") REFERENCES " + TABLE_FOODS + "(" + COLUMN_FOOD_ID + "),"
                + "FOREIGN KEY (" + COLUMN_CART_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_CART_TABLE);
        OrderDatabase.createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       if (oldVersion < 7){
            // Thêm cột mới mẫu : db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_FULL_NAME + " TEXT");
           OrderDatabase.createTables(db);
        }


          /*Cập nhật khi thêm bảng mới
        if (oldVersion < x){}
       **/
    }

    // Thêm người dùng mới
    public boolean addUser(Users user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PHONE, user.getPhoneNumber());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_ADDRESS, user.getAddress());
        values.put(COLUMN_FULL_NAME, user.getFullname());

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Kiểm tra người dùng đã tồn tại chưa
    public boolean checkUserExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_PHONE},
                COLUMN_PHONE + "=?",
                new String[]{phone},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public Users checkLogin(String phone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USER_ID + ", " + COLUMN_PHONE + ", " + COLUMN_EMAIL + ", "+COLUMN_PASSWORD + "," + COLUMN_ADDRESS +", "+COLUMN_FULL_NAME +
                " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_PHONE + " = ? AND " + COLUMN_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{phone, password});

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            String phoneResult = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            String pass = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
            String fullname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME));
            cursor.close();
            return new Users(id,email,phoneResult, pass,address, fullname);
        }

        if (cursor != null) cursor.close();
        return null;
    }

    //====================XỬ LÝ DỮ LIỆU CATEGORY
    //  Thêm category mới
    public void insertCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, category.getId());
        values.put(COLUMN_IMAGE_PATH, category.getImagePath());
        values.put(COLUMN_NAME, category.getName());
        db.insert(TABLE_CATEGORY, null, values);
        db.close();
    }

    // 👉 Lấy danh sách category
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                list.add(new Category(id, imagePath, name));
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();
        return list;

    }

    public String getCategoryNameById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = "";

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(categoryId)});
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;

    }

}
