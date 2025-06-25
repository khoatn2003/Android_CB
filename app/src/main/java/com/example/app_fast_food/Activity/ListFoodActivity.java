package com.example.app_fast_food.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_fast_food.Adapter.ListFoodAdapter;
import com.example.app_fast_food.Helper.DatabaseHelper;
import com.example.app_fast_food.Helper.FoodsDatabase;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ActivityDetailFoodBinding;
import com.example.app_fast_food.databinding.ActivityListFoodBinding;

import java.util.ArrayList;
import java.util.List;

public class ListFoodActivity extends BaseActivity {
    ActivityListFoodBinding binding;
    DatabaseHelper db;
    FoodsDatabase foodDB;
    private List<Foods> foodList;

    ListFoodAdapter listFoodAdapter;
    private int categoryId = -1; // Giá trị mặc định nếu không nhận được ID
    private String categoryName = "Sản phẩm"; // Tên

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setAction();
        initListFood();
        loadFoodCategory();
    }

    private void setAction() {
        binding.backBtn.setOnClickListener(view -> {
            finish();
        });

    }

    private void initListFood(){
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            categoryId = intent.getIntExtra("categoryID", -1);
            categoryName = intent.getStringExtra("categoryName");
            if (categoryId == -1 || categoryName == null) {
                categoryId =0;
                categoryName = "Pizza"; // Đặt tên mặc định nếu không có
            }

        }
        binding.titleTxt.setText(categoryName);
        foodList = new ArrayList<>();
        listFoodAdapter = new ListFoodAdapter(this, foodList);
        // binding.foodRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Nếu dùng ViewBinding
        // binding.foodRecyclerView.setAdapter(foodListAdapter);
        // Hoặc:
        RecyclerView foodRecyclerView = findViewById(R.id.foodListView); // Đảm bảo ID này đúng
        foodRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        foodRecyclerView.setAdapter(listFoodAdapter);

    }

    private void loadFoodCategory() {
        db = new DatabaseHelper(this);
        foodDB = new FoodsDatabase(db.getReadableDatabase());
        binding.progressBar.setVisibility(View.VISIBLE);
        if (categoryId != -1) {
            List<Foods> foodsFromDb = foodDB.getFoodsByCategoryId(categoryId);
            binding.progressBar.setVisibility(View.GONE);
            if (foodsFromDb != null && !foodsFromDb.isEmpty()) {
                listFoodAdapter.updateData(foodsFromDb);
            }
        }
    }
}

