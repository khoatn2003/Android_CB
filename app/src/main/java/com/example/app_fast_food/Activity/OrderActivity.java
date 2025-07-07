package com.example.app_fast_food.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_fast_food.Adapter.OrderAdapter;
import com.example.app_fast_food.Helper.OrderDatabase;
import com.example.app_fast_food.Order.Order;
import com.example.app_fast_food.Order.OrderItem;
import com.example.app_fast_food.Order.OrderItemDisplay;
import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ActivityOrderBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    ActivityOrderBinding binding;
    private TabLayout tabLayout;
    private FrameLayout contentContainer;
    private OrderAdapter orderAdapter;
    public static final int REQUEST_ORDER_DETAIL = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(view -> finish());

        Order order = new Order();
        tabLayout = findViewById(R.id.order_status_tabs);
        contentContainer = findViewById(R.id.order_content_container);

        // Thêm tab
        tabLayout.addTab(tabLayout.newTab().setText("Chờ xác nhận"));
        tabLayout.addTab(tabLayout.newTab().setText("Chờ lấy hàng"));
        tabLayout.addTab(tabLayout.newTab().setText("Đang giao"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã giao"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã hủy"));

        // Hiển thị nội dung ban đầu
        if (tabLayout.getTabCount() > 0) {
            TabLayout.Tab firstTab = tabLayout.getTabAt(0);
            if (firstTab != null) {
                firstTab.select();
                loadOrderItemsByStatus(firstTab.getText().toString());
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String title = tab.getText().toString();
                loadOrderItemsByStatus(title);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    binding.imageView5.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Intent intent = new Intent(OrderActivity.this, SearchOrderActivity.class);
                intent.putExtra("shouldFocus", true);
                startActivity(intent);
                return true;
            }
            return false;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderItemsByStatus("Chờ xác nhận");
    }

    private void loadOrderItemsByStatus(String status) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_recycler_orders, contentContainer, false);
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewOrders); // view đã inflate
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int userId = sp.getInt("userId", -1);
        OrderDatabase db = new OrderDatabase(this);
        List<OrderItemDisplay> list = db.getOrderItemsByStatus(userId, status);
        if(list.isEmpty()){
            // Không có sản phẩm → hiện thông báo và ảnh
            View emptyView = LayoutInflater.from(this).inflate(R.layout.layout_empty_order, contentContainer, false);
            contentContainer.removeAllViews();
            contentContainer.addView(emptyView);
        }else{
            if (orderAdapter == null) {
                orderAdapter = new OrderAdapter(this, new ArrayList<>());
                orderAdapter.setOnOrderChangedListener(() -> {
                    loadOrderItemsByStatus(status);
                });
            }
            recyclerView.setAdapter(orderAdapter);
            Collections.reverse(list);
            orderAdapter.setData(list);

            contentContainer.removeAllViews();
            contentContainer.addView(view);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ORDER_DETAIL && resultCode == RESULT_OK && data != null){
            boolean isCancelled = data.getBooleanExtra("order_cancelled", false);
            if (isCancelled) {
                String prevStatus = data.getStringExtra("previous_status");
                if (prevStatus != null) {
                    loadOrderItemsByStatus(prevStatus);
                }
            }
        }
    }
}