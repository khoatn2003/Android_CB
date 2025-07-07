package com.example.app_fast_food.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_fast_food.Adapter.ThanhToanAdapter;
import com.example.app_fast_food.Helper.CartDatabase;
import com.example.app_fast_food.Cart.CartItem;
import com.example.app_fast_food.Adapter.dinhDangTien;

import com.example.app_fast_food.Helper.FoodsDatabase;
import com.example.app_fast_food.Helper.OrderDatabase;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.Order.Order;
import com.example.app_fast_food.Order.OrderItem;
import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ActivityThanhToanBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ThanhToanActivity extends AppCompatActivity {
    ActivityThanhToanBinding binding;
    private List<CartItem> cartItemList;
    private ThanhToanAdapter thanhToanAdapter;
    private FoodsDatabase foodDB;
    private static final int REQUEST_EDIT_ITEM_PAYMENT = 1010;
    private static final int REQUEST_EDIT_ADDRESS = 102;
    private double delivery = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThanhToanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String phone = sp.getString("phone", "");
            String address = sp.getString("address","");
            String fullname =sp.getString("fullname","");
            // Bạn có thể sử dụng userId, phone để lấy thêm dữ liệu nếu cần
            binding.txtUserName.setText(fullname);
            binding.txtAddress.setText(address);
            binding.txtPhone.setText(phone);
        }

        cartItemList = (List<CartItem>) getIntent().getSerializableExtra("cart_items");
        loadSP();
        setupListeners();
        radioClick();
        updateUI();

        binding.btnBack.setOnClickListener(view -> finish());
        binding.btnEditAddress.setOnClickListener(v->{
            Intent intent = new Intent(this, SelectAddressActivity.class);
            startActivityForResult(intent, REQUEST_EDIT_ADDRESS);
        });
        binding.btnEditName.setOnClickListener(view -> showEditReceiverDialog());
        binding.btnEditPhone.setOnClickListener(view -> showEditReceiverDialog());
        binding.btnConfirmOrder.setOnClickListener(v->{
            String paymentMethod = getSelectedPaymentMethod();
            if (paymentMethod == null) {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }
            String receiverName = binding.txtUserName.getText().toString();
            String address = binding.txtAddress.getText().toString();
            String phone = binding.txtPhone.getText().toString();

            if (receiverName.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Thông tin người nhận không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            double totalPrice = tinhTongTien(cartItemList) + delivery;
            insertOrderToDatabase(receiverName, address, phone, paymentMethod, totalPrice);
        });

    }

    private void loadSP(){
        // Gắn vào RecyclerView
        if(cartItemList.size() >0){
        RecyclerView recyclerView = findViewById(R.id.rvOrderItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        thanhToanAdapter = new ThanhToanAdapter(cartItemList,this);
        recyclerView.setAdapter(thanhToanAdapter);
    }else{
            Log.d("Lỗi ở ", "Ko load dc dữ liệu" + cartItemList.size());
        }
    }
    private void updateUI() {
        tinhTongTien(cartItemList);
        double tongTienSP = tinhTongTien(cartItemList);
        double tongTienBill = tongTienSP + delivery;
        String hienThiTienSP = dinhDangTien.dinhdang(tongTienSP);
        String hienThiTT = dinhDangTien.dinhdang(tongTienBill);
        binding.txtSubtotalValue.setText(hienThiTienSP);
        binding.txtTotalAmount.setText(hienThiTT);
    }

    private void setupListeners() {
        thanhToanAdapter.setOnEditItemClickListener(position -> {
            FoodsDatabase foodDB = new FoodsDatabase(this);
            CartItem cartItem = cartItemList.get(position);
            Foods food = foodDB.getFoodById(cartItem.getFoodId());

            if (food != null) {
                Intent intent = new Intent(this, EditFoodActivity.class);
                intent.putExtra("food", food);
                intent.putExtra("cart", cartItem);
                startActivityForResult(intent, REQUEST_EDIT_ITEM_PAYMENT);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_ITEM_PAYMENT && resultCode == RESULT_OK && data != null) {
            CartItem updatedItem = (CartItem) data.getSerializableExtra("updatedCartItem");

            if (updatedItem.getQuantity() == 0) {
                for (int i = 0; i < cartItemList.size(); i++) {
                    if (cartItemList.get(i).getCartId() == updatedItem.getCartId()) {
                        cartItemList.remove(i);
                        break;
                    }
                }
            } else{
                for (int i = 0; i < cartItemList.size(); i++) {
                    if (cartItemList.get(i).getCartId() == updatedItem.getCartId()) {
                        cartItemList.set(i, updatedItem);
                        break;
                    }
                }
            }
            Log.d("Số lượng", "là:" + updatedItem.getQuantity());
            thanhToanAdapter.notifyDataSetChanged();
            updateUI();

        }
        if (requestCode == REQUEST_EDIT_ADDRESS && resultCode == RESULT_OK && data !=null) {
            String selectedAddress = data.getStringExtra("selected_address");
            binding.txtAddress.setText(selectedAddress);
        }
    }

    private void insertOrderToDatabase(String receiverName, String address, String phone,
                                       String paymentMethod, double totalPrice) {
        OrderDatabase orderDb = new OrderDatabase(this);

        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        int userId = sp.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Lỗi: không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = UUID.randomUUID().toString().substring(0, 8);
        // 1. Tạo đơn hàng
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setReceiverName(receiverName);
        order.setShippingAddress(address);
        order.setPhone(phone);
        order.setTotalPrice(totalPrice);
        // Tạo ngày giờ hiện tại
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        order.setOrderDate(currentDateTime);

        boolean isBuyNow = getIntent().getBooleanExtra("from_buy_now", false);
        boolean success = orderDb.insertOrder(order);
        if (!success) {
            Toast.makeText(this, "Tạo đơn hàng thất bại", Toast.LENGTH_SHORT).show();
            return;
        }
        // 2. Thêm sản phẩm vào bảng OrderItem
        List<OrderItem> orderItems = new ArrayList<>();
        String[] testStatuses = {"Chờ xác nhận"};
        for (String status : testStatuses) {
            for (CartItem cartItem : cartItemList) {
                String shippingTime = null;
                String receiveTime = null;
                if (status.equals("Đang giao") || status.equals("Đã giao")) {
                    shippingTime = addMinutesToTime(currentDateTime, 15); // orderTime + 15 phút
                    if (status.equals("Đã giao")) {
                        receiveTime = addMinutesToTime(shippingTime, 30); // shippingTime + 30 phút
                    }
                }
                OrderItem oi = OrderItem.fromCartItem(cartItem, orderId, status, shippingTime, receiveTime);
                orderItems.add(oi);
            }
        }
        order.setItems(orderItems);
        orderDb.insertOrderItems(order.getItems());

        // 3. Ghi thông tin thanh toán
        orderDb.insertPayment(orderId, paymentMethod);

        // 4. Xoá giỏ hàng của user
        if (!isBuyNow) {
            CartDatabase helper = new CartDatabase(this);
            helper.clearCart(userId); // chỉ xóa nếu KH mua từ giỏ hàng
        }
        showOrderSuccessDialog();
    }

    public void showOrderSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_order_success, null);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        View dialogView = dialog.getWindow().getDecorView();
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        dialogView.startAnimation(bounce);

        Button btnTrackOrder = view.findViewById(R.id.btnTrackOrder);
        TextView tvGoHome = view.findViewById(R.id.tvGoHome);
        ImageView btnBack = view.findViewById(R.id.btn_back);

        btnTrackOrder.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderActivity.class));
            dialog.dismiss();
            finish();
        });

        tvGoHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            dialog.dismiss();
        });
        
        btnBack.setOnClickListener(v->{
            startActivity(new Intent(this, MainActivity.class));
            dialog.dismiss();
        });

    }
    private void showEditReceiverDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_receiver, null);

        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtPhone = dialogView.findViewById(R.id.edtPhone);

        // Gán giá trị hiện tại
        edtName.setText(binding.txtUserName.getText().toString());
        edtPhone.setText(binding.txtPhone.getText().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thông tin người nhận")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newName = edtName.getText().toString().trim();
                    String newPhone = edtPhone.getText().toString().trim();

                    // Kiểm tra hợp lệ
                    if (!newName.isEmpty()) {
                        binding.txtUserName.setText(newName);
                        binding.txtPhone.setText(newPhone);
                    } else {
                        Toast.makeText(this, "Tên hoặc số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null);
        builder.show();

    }


    public double tinhTongTien(List<CartItem> cartItemList) {
        double tong = 0;
        for (CartItem item : cartItemList) {
            tong += item.getToTalPrice(); // hoặc item.getPricePerItem() * item.getQuantity()
        }
        return tong;
    }
    private void radioClick(){
        View.OnClickListener radioClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset tất cả RadioButton
                binding.radioMoneyCash.setChecked(false);
                binding.radioCard.setChecked(false);
                binding.radioWallet.setChecked(false);

                // Chỉ set Checked cho cái được bấm
                ((RadioButton) v).setChecked(true);
            }
        };

// Gán listener cho từng RadioButton
        binding.radioMoneyCash.setOnClickListener(radioClickListener);
        binding.radioCard.setOnClickListener(radioClickListener);
        binding.radioWallet.setOnClickListener(radioClickListener);
    }
    private String getSelectedPaymentMethod() {
        if (binding.radioMoneyCash.isChecked()) return "Tiền mặt";
        if (binding.radioCard.isChecked()) return "Thẻ";
        if (binding.radioWallet.isChecked()) return "Ví điện tử";
        return null;
    }
    private String addMinutesToTime(String time, int minutesToAdd) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.setTime(date);
                calendar.add(Calendar.MINUTE, minutesToAdd);
                return sdf.format(calendar.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}