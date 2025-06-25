package com.example.app_fast_food.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_fast_food.Adapter.ThanhToanAdapter;
import com.example.app_fast_food.Cart.CartItem;
import com.example.app_fast_food.Adapter.dinhDangTien;

import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ActivityCartBinding;
import com.example.app_fast_food.databinding.ActivityMainBinding;
import com.example.app_fast_food.databinding.ActivityThanhToanBinding;

import java.util.ArrayList;
import java.util.List;

public class ThanhToanActivity extends AppCompatActivity {
    ActivityThanhToanBinding binding;
    private List<CartItem> cartItemList;
    private ThanhToanAdapter thanhToanAdapter;
    private double delivery = 15000;

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
        radioClick();
        binding.btnBack.setOnClickListener(view -> finish());

        double tongTienSP = tinhTongTien(cartItemList);
        double tongTienBill = tongTienSP + delivery;
        String hienThiTienSP = dinhDangTien.dinhdang(tongTienSP);
        String hienThiTT = dinhDangTien.dinhdang(tongTienBill);
        binding.txtSubtotalValue.setText(hienThiTienSP);
        binding.txtTotalAmount.setText(hienThiTT);

        thanhToanAdapter.setOnEditQuantityClickListener(position -> {
            // Mở dialog, xử lý update...
            Toast.makeText(this,"Đây là sản phẩm"+position,Toast.LENGTH_SHORT).show();
        });

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

    private void loadSP(){
        // Gắn vào RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvOrderItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        thanhToanAdapter = new ThanhToanAdapter(cartItemList,this);
        recyclerView.setAdapter(thanhToanAdapter);
    }
}