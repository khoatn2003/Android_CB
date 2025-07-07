package com.example.app_fast_food.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_fast_food.Model.Users;
import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ActivitySelectAddressBinding;

import java.util.HashSet;
import java.util.Set;

public class SelectAddressActivity extends AppCompatActivity {

    ActivitySelectAddressBinding biding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        biding = ActivitySelectAddressBinding.inflate(getLayoutInflater());
        setContentView(biding.getRoot());

        RadioGroup radioGroup = findViewById(R.id.radioGroupAddresses);

        SharedPreferences sp2 = getSharedPreferences("address_temp", MODE_PRIVATE);
        Set<String> tempAddresses = new HashSet<>(sp2.getStringSet("temp_addresses", new HashSet<>()));

        // Địa chỉ từ SQLite
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String phone = sp.getString("phone", "");
            String address = sp.getString("address","");
            // Bạn có thể sử dụng userId, phone để lấy thêm dữ liệu nếu
            RadioButton rbDefault = new RadioButton(this);
            rbDefault.setText(address + " (Mặc định)");
            rbDefault.setTextSize(14);
            rbDefault.setId(View.generateViewId());
            radioGroup.addView(rbDefault);
        }

        for (String addr : tempAddresses) {
            RadioButton rb = new RadioButton(this);
            rb.setText(addr);
            rb.setId(View.generateViewId());
            rb.setTextSize(14);
            radioGroup.addView(rb);
        }

        findViewById(R.id.layoutAddAddress).setOnClickListener(v -> {
            // Hiển thị dialog thêm địa chỉ mới
            showAddAddressDialog();
        });

        radioGroup.setOnCheckedChangeListener((group, checkedID)->{
            RadioButton selected = group.findViewById(checkedID);
            String selectedAddress = selected.getText().toString();
            if (selectedAddress.contains("(Mặc định)")) {
                selectedAddress = selectedAddress.replace(" (Mặc định)", "").trim();
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_address", selectedAddress);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        biding.btnBack.setOnClickListener(view -> finish());
    }
    private void showAddAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm địa chỉ mới");

        final EditText input = new EditText(this);
        input.setHint("Nhập địa chỉ");
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newAddress = input.getText().toString();
            if (!newAddress.isEmpty()) {
                // Lưu vào SharedPreferences
                SharedPreferences sp = getSharedPreferences("address_temp", MODE_PRIVATE);
                Set<String> savedAddresses = sp.getStringSet("temp_addresses", new HashSet<>());
                savedAddresses.add(newAddress);
                sp.edit().putStringSet("temp_addresses", savedAddresses).apply();

                //db.insertAddress(userId, newAddress); // Không phải mặc định
                recreate(); // Load lại giao diện để hiển thị thêm radio
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

}