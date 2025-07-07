package com.example.app_fast_food.Activity;

import android.app.ComponentCaller;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_fast_food.Adapter.SearchFoodAdapter;
import com.example.app_fast_food.Adapter.SearchOrderAdapter;
import com.example.app_fast_food.Helper.OrderDatabase;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.Order.OrderItem;
import com.example.app_fast_food.Order.OrderItemDisplay;
import com.example.app_fast_food.R;
import com.example.app_fast_food.Search.SearchHistoryManager;
import com.example.app_fast_food.databinding.ActivitySearchOrderBinding;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchOrderActivity extends AppCompatActivity {
    ActivitySearchOrderBinding binding;
    private ProgressBar progressBar;
    private SearchOrderAdapter adapter;
    private OrderDatabase orDB;

    public static final int REQUEST_ORDER_SEARCH = 185;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySearchOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        int userId = sp.getInt("userId", -1);
        progressBar = findViewById(R.id.progressBar);

        orDB = new OrderDatabase(this);
        adapter = new SearchOrderAdapter(this, new ArrayList<>());
        binding.recyclerViewResult.setAdapter(adapter);
        binding.recyclerViewResult.setLayoutManager(new LinearLayoutManager(this));
        loadAllOrder(userId);

        searchEvent(userId);
        searchChance(userId);
        binding.btnBack.setOnClickListener(view -> finish());
        binding.btnExploreOrder.setOnClickListener(view -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ORDER_SEARCH && resultCode == RESULT_OK && data != null){
            SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
            int userId = sp.getInt("userId", -1);
            boolean isCancelled = data.getBooleanExtra("order_cancelled", false);

            if (isCancelled) {
                loadAllOrder(userId);
            }
        }
    }

    //Xử lý tìm kiếm bằng ID hoặc tên đơn hàng
    public void searchEvent(int userId){
            binding.searchEdt.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            binding.searchEdt.setOnEditorActionListener((v, actionId, event) -> {
                binding.progressBar.setVisibility(View.VISIBLE);
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    String keyword = binding.searchEdt.getText().toString().trim();
                    if (keyword != null) {
                        binding.recyclerViewResult.setVisibility(View.INVISIBLE);
                        new Handler().postDelayed(() -> {
                            List<OrderItemDisplay> itemOrder = orDB.getAllOrderItem(userId);
                            List<OrderItemDisplay> filtered = searchOrder(itemOrder, keyword);
                            showSearchResult(filtered, keyword);
                            Log.d("Kết quả", "Tìm kiếm đơn hàng: " + filtered.size());
                            showSearchResult(filtered, "Kết quả cho đơn hàng \"" + keyword + "\"");
                            progressBar.setVisibility(View.GONE);
                            binding.recyclerViewResult.setVisibility(View.VISIBLE);
                        }, 400);

                    }
                    return true;
                }
                return false;

            });
    }

    private List<OrderItemDisplay> searchOrder(List<OrderItemDisplay> orderItemDisplayList, String searchText) {
        List<OrderItemDisplay> result = new ArrayList<>();
        String keyword = removeDiacritics(searchText).toLowerCase();
        for (OrderItemDisplay itemOrder : orderItemDisplayList) {
            String foodTitle = removeDiacritics(itemOrder.getFoodTitle()).toLowerCase();
            String orderID = removeDiacritics(itemOrder.getOrderID()).toLowerCase();
            if (foodTitle.contains(keyword) || orderID.contains(keyword)) {
                result.add(itemOrder);
            }

        }
        return result;
    }

    private String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace("đ", "d")
                .replace("Đ", "D");
    }

    private void showSearchResult(List<OrderItemDisplay> results, String titleIfAny) {
        adapter.setData(results);

        boolean isEmpty = results == null || results.isEmpty();
        findViewById(R.id.layoutEmptyResult).setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        findViewById(R.id.recyclerViewResult).setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        findViewById(R.id.txtSuggestedTitle).setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        findViewById(R.id.txtRecent).setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        findViewById(R.id.recentKeywordsContainer).setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (!isEmpty && titleIfAny != null) {
            ((TextView) findViewById(R.id.txtSuggestedTitle)).setText(titleIfAny);
        }
    }

    private void searchChance(int userID) {
        binding.searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().isEmpty()) {
                        progressBar.setVisibility(View.VISIBLE);
                        binding.recyclerViewResult.setVisibility(View.INVISIBLE);
                        new Handler().postDelayed(() -> {
                            loadAllOrder(userID); // Tải lại danh sách đề xuất
                            findViewById(R.id.layoutEmptyResult).setVisibility(View.GONE);
                            findViewById(R.id.txtRecent).setVisibility(View.VISIBLE);
                            findViewById(R.id.recentKeywordsContainer).setVisibility(View.VISIBLE);
                            findViewById(R.id.txtSuggestedTitle).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.txtSuggestedTitle)).setText("Tất cả đơn hàng");
                            progressBar.setVisibility(View.GONE);
                            binding.recyclerViewResult.setVisibility(View.VISIBLE);
                        }, 400);

                    }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

        });
    }
    private void loadAllOrder(int userId) {
        OrderDatabase orDB = new OrderDatabase(this);
        List<OrderItemDisplay> allOrder = orDB.getAllOrderItem(userId);
        adapter.setData(allOrder);
        adapter.notifyDataSetChanged();
    }
}