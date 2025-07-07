package com.example.app_fast_food.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.app_fast_food.Cart.CartItem;
import com.example.app_fast_food.Order.Order;
import com.example.app_fast_food.Order.OrderItem;
import com.example.app_fast_food.Order.OrderItemDisplay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderDatabase {
    private SQLiteDatabase db;

    // Tên bảng
    public static final String TABLE_ORDERS = "Orders";
    public static final String TABLE_ORDER_ITEM = "OrderItem";
    public static final String TABLE_PAYMENT = "Payment";

    // Cột bảng Orders
    public static final String COLUMN_ORDER_ID = "Order_id";
    public static final String COLUMN_ORDER_USER_ID = "userID";
    public static final String COLUMN_TOTAL_PRICE = "Total_price";
    public static final String COLUMN_ORDER_DATE = "Order_date";
    public static final String COLUMN_RECEIVER_NAME = "Receiver_name";
    public static final String COLUMN_SHIPPING_ADDRESS = "Shipping_address";
    public static final String COLUMN_ORDER_PHONE = "Phone";

    // Cột bảng OrderItem
    public static final String COLUMN_ORDER_ITEM_ID = "order_item_id";
    public static final String COLUMN_ORDER_ITEM_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_ITEM_FOOD_ID = "food_id";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_PRICE_PER_ITEM = "price_per_item";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_ITEM_STATUS = "status";
    public static final String COLUMN_DELIVERY_TIME ="delivery_time";
    public static final String COLUMN_RECEIVE_TIME ="receive_time";
    public static final String COLUMN_CANCEL_TIME = "cancel_time";

    // Cột bảng Payment
    public static final String COLUMN_PAYMENT_ID = "payment_id";
    public static final String COLUMN_PAYMENT_ORDER_ID = "order_id";
    public static final String COLUMN_METHOD = "method";
    public static final String COLUMN_PAYMENT_DATE = "payment_date";

    public OrderDatabase(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public static void createTables(SQLiteDatabase db) {
        String CREATE_ORDERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ORDERS + "("
                + COLUMN_ORDER_ID + " TEXT PRIMARY KEY, "
                + COLUMN_ORDER_USER_ID + " INTEGER NOT NULL, "
                + COLUMN_TOTAL_PRICE + " REAL NOT NULL, "
                + COLUMN_ORDER_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP, "
                + COLUMN_RECEIVER_NAME + " TEXT NOT NULL, "
                + COLUMN_SHIPPING_ADDRESS + " TEXT NOT NULL, "
                + COLUMN_ORDER_PHONE + " TEXT NOT NULL, "
                + "FOREIGN KEY (" + COLUMN_ORDER_USER_ID + ") REFERENCES Users(userID))";

        String CREATE_ORDER_ITEM_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ORDER_ITEM + "("
                + COLUMN_ORDER_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ORDER_ITEM_ORDER_ID + " INTEGER NOT NULL, "
                + COLUMN_ORDER_ITEM_FOOD_ID + " INTEGER NOT NULL, "
                + COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + COLUMN_PRICE_PER_ITEM + " REAL NOT NULL, "
                + COLUMN_NOTE + " TEXT, "
                + COLUMN_ITEM_STATUS + " TEXT DEFAULT 'Chờ xác nhận', "
                + COLUMN_DELIVERY_TIME + " TEXT, "
                + COLUMN_RECEIVE_TIME + " TEXT, "
                + COLUMN_CANCEL_TIME + " TEXT, "


                + "FOREIGN KEY (" + COLUMN_ORDER_ITEM_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), "
                + "FOREIGN KEY (" + COLUMN_ORDER_ITEM_FOOD_ID + ") REFERENCES Foods(food_id))";

        String CREATE_PAYMENT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PAYMENT + "("
                + COLUMN_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PAYMENT_ORDER_ID + " INTEGER NOT NULL, "
                + COLUMN_METHOD + " TEXT NOT NULL, "
                + COLUMN_PAYMENT_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (" + COLUMN_PAYMENT_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "))";

        // Thực thi câu lệnh
        db.execSQL(CREATE_ORDERS_TABLE);
        db.execSQL(CREATE_ORDER_ITEM_TABLE);
        db.execSQL(CREATE_PAYMENT_TABLE);
    }
    public boolean insertOrder(Order order) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, order.getOrderId());
        values.put(COLUMN_ORDER_USER_ID, order.getUserId());
        values.put(COLUMN_TOTAL_PRICE, order.getTotalPrice());
        values.put(COLUMN_RECEIVER_NAME, order.getReceiverName());
        values.put(COLUMN_SHIPPING_ADDRESS, order.getShippingAddress());
        values.put(COLUMN_ORDER_PHONE, order.getPhone());

        values.put(COLUMN_ORDER_DATE, order.getOrderDate());

        long result = db.insert(TABLE_ORDERS, null, values);
        return result != -1;
    }
    public void insertOrderItems(List<OrderItem> items) {
        // Thêm mới
        for (OrderItem item : items) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ORDER_ID, item.getOrderId());
            values.put(COLUMN_ORDER_ITEM_FOOD_ID, item.getFoodId());
            values.put(COLUMN_QUANTITY, item.getQuantity());
            values.put(COLUMN_PRICE_PER_ITEM, item.getPricePerItem());
            values.put(COLUMN_NOTE, item.getNote());
            values.put(COLUMN_ITEM_STATUS, item.getStatus());
            values.put(COLUMN_DELIVERY_TIME, item.getDeliveryTime());
            values.put(COLUMN_RECEIVE_TIME, item.getReceiveTime());
            db.insert(TABLE_ORDER_ITEM, null, values);

        }

    }
    public long insertPayment(String orderId, String method) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, orderId);
        values.put(COLUMN_METHOD, method);

        return db.insert(TABLE_PAYMENT, null, values);
    }

    public List<OrderItemDisplay> getOrderItemsByStatus(int userId, String status) {
        List<OrderItemDisplay> list = new ArrayList<>();

        String query = "SELECT o.Order_id, sp.title, sp.image_path, oi.quantity, oi.price_per_item, oi.status, o.Order_date, o.Shipping_address, p.method, oi.note," +
                " oi.delivery_time, oi.receive_time, oi.cancel_time, oi.order_item_id, sp.food_id " +
                "FROM OrderItem oi " +
                "JOIN Orders o ON o.Order_id = oi.order_id " +
                "JOIN Foods sp ON sp.food_id = oi.food_id " +
                "LEFT JOIN Payment p ON p.order_id = o.Order_id "+
                "WHERE o.userID = ? AND oi.status = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), status});
        if (cursor.moveToFirst()) {
            do {
                OrderItemDisplay item = new OrderItemDisplay();
                item.setOrderID(cursor.getString(0));
                item.setFoodTitle(cursor.getString(1));
                item.setImagePath(cursor.getString(2));
                item.setQuantity(cursor.getInt(3));
                item.setPricePerItem(cursor.getDouble(4));
                item.setStatus(cursor.getString(5));
                item.setOrderDateItem(cursor.getString(6));
                item.setAddressOrder(cursor.getString(7));
                item.setPaymentMethod(cursor.getString(8));
                item.setNote(cursor.getString(9));
                item.setOrderShippingTime(cursor.getString(10));
                item.setOrderReceiveTime(cursor.getString(11));
                item.setOrderCancelTime(cursor.getString(12));
                item.setOrderItemID(cursor.getInt(13));
                item.setFoodID(cursor.getInt(14));
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean cancelOrderItem(int orderItemId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_STATUS, "Đã hủy");

        // Thời gian hiện tại
        String cancelTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put(COLUMN_CANCEL_TIME, cancelTime);

        int result = db.update(
                TABLE_ORDER_ITEM,
                values,
                COLUMN_ORDER_ITEM_ID + " = ? AND (" + COLUMN_ITEM_STATUS + " = ? OR " + COLUMN_ITEM_STATUS + " = ?)",
                new String[]{String.valueOf(orderItemId), "Chờ xác nhận", "Chờ lấy hàng"}
        );

        return result > 0;
    }

    public List<OrderItemDisplay> getAllOrderItem(int userId) {
        List<OrderItemDisplay> list = new ArrayList<>();

        String query = "SELECT o.Order_id, sp.title, sp.image_path, oi.quantity, oi.price_per_item, oi.status, o.Order_date, o.Shipping_address, p.method, oi.note," +
                " oi.delivery_time, oi.receive_time, oi.cancel_time, oi.order_item_id, sp.food_id " +
                "FROM OrderItem oi " +
                "JOIN  Orders o ON o.order_id = oi.order_id " +
                "JOIN Foods sp ON sp.food_id = oi.food_id " +
                "LEFT JOIN Payment p ON p.order_id = o.Order_id "+
                "WHERE o.userID = ?" +
                "ORDER BY o.Order_date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                OrderItemDisplay item = new OrderItemDisplay();
                item.setOrderID(cursor.getString(0));
                item.setFoodTitle(cursor.getString(1));
                item.setImagePath(cursor.getString(2));
                item.setQuantity(cursor.getInt(3));
                item.setPricePerItem(cursor.getDouble(4));
                item.setStatus(cursor.getString(5));
                item.setOrderDateItem(cursor.getString(6));
                item.setAddressOrder(cursor.getString(7));
                item.setPaymentMethod(cursor.getString(8));
                item.setNote(cursor.getString(9));
                item.setOrderShippingTime(cursor.getString(10));
                item.setOrderReceiveTime(cursor.getString(11));
                item.setOrderCancelTime(cursor.getString(12));
                item.setOrderItemID(cursor.getInt(13));
                item.setFoodID(cursor.getInt(14));
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}

