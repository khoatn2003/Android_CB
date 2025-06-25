package com.example.app_fast_food.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ActivityIntroBinding;

public class IntroActivity extends BaseActivity {
ActivityIntroBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
        getWindow().setStatusBarColor(Color.parseColor("#FFE4B5"));


    }

    private void setVariable() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        binding.loginBtn.setOnClickListener(v->{
            startActivity(new Intent(IntroActivity.this,LoginActivity.class));
        });

        //DUNG CHO LUU TTIN DANG NHAP
        binding.loginBtn.setOnClickListener(v -> {
            if(isLoggedIn){
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
            }else{
                startActivity(new Intent(IntroActivity.this,MainActivity.class));
            }
        });

        binding.signupBtn.setOnClickListener(v -> {
            startActivity(new Intent(IntroActivity.this,SignupActivity.class));
        });
    }


}