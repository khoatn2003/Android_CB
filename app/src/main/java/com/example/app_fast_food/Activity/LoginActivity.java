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
import com.example.app_fast_food.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        setVariable();
    }

    private void setVariable() {
        binding.loginBtn.setOnClickListener(v->{
            String phone=binding.userEdt.getText().toString();
            String password=binding.passEdt.getText().toString();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Users users = dbHelper.checkLogin(phone,password);

            if (users != null) {
                // Đăng nhập thành công
                SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("userId", users.getUserID());
                editor.putString("phone", users.getPhoneNumber());
                editor.putString("email", users.getEmail());
                editor.putString("address", users.getAddress());
                editor.putString("fullname", users.getFullname());
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

               startActivity(new Intent(LoginActivity.this, MainActivity.class));
               finish();
           }else{
               Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
           }
        });

        binding.txtLogin.setOnClickListener(v->{
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

    }
}