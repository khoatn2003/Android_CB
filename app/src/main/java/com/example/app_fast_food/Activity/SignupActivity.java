package com.example.app_fast_food.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_fast_food.Helper.DatabaseHelper;
import com.example.app_fast_food.Model.Users;
import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ActivityIntroBinding;
import com.example.app_fast_food.databinding.ActivitySignupBinding;

public class SignupActivity extends BaseActivity {
    ActivitySignupBinding binding;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper(this);
        setVariable();

    }

    private void setVariable() {
        binding.signupBtn.setOnClickListener(v -> {
            String phone = binding.phoneEdt.getText().toString().trim(); // userEdt dùng để nhập số điện thoại
            String password = binding.passEdt.getText().toString().trim();
            String email = binding.emailEdt.getText().toString().trim(); // nếu không dùng email, để trống hoặc bạn có thể dùng số điện thoại làm email giả
            String address = binding.locationEdt.getText().toString().trim(); // tạo thêm EditText nếu chưa có
            String fullname = binding.fullNameEdt.getText().toString().trim();
            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (db.checkUserExists(phone)) {
                Toast.makeText(this, "Số điện thoại đã được sử dụng.Vui lòng đăng ký bằng số điện thoại khác", Toast.LENGTH_SHORT).show();
                return;
            }

            Users newUser = new Users(email,phone,password,address,fullname);
            boolean check = db.addUser(newUser);
            if (check) {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                //luu email da dang ky tren may
                /*
                SharedPreferences preferences = getSharedPreferences("USER_PREF", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("email", email); // email là thông tin người dùng đăng nhập
                editor.apply();
                */

                // chuyển về màn hình đăng nhập
                finish(); // hoặc dùng startActivity(new Intent(...));
            } else {
                Toast.makeText(this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.layoutTextView5.setOnClickListener(v->{
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }
}