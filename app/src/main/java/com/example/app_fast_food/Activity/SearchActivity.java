package com.example.app_fast_food.Activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_fast_food.Adapter.SearchFoodAdapter;
import com.example.app_fast_food.Helper.DatabaseHelper;
import com.example.app_fast_food.Helper.FoodsDatabase;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.R;
import com.google.android.flexbox.FlexboxLayout;
import com.example.app_fast_food.Search.SearchHistoryManager;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText searchEdt;
    private ImageView back;
    private RecyclerView recyclerView;
    private SearchFoodAdapter adapter;
    private DatabaseHelper dbHelper;
    private FoodsDatabase foodsDB;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEdt = findViewById(R.id.searchEdt);
        back = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerViewResult);
        dbHelper = new DatabaseHelper(this);
        foodsDB = new FoodsDatabase(this);
        adapter = new SearchFoodAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        loadSuggestedFoods();
        searchEdt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        searchEdt.setOnEditorActionListener((v, actionId, event) -> {
            progressBar.setVisibility(View.VISIBLE);
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String keyword = searchEdt.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    new Handler().postDelayed(() -> {
                        SearchHistoryManager.saveKeyword(this, keyword);
                        searchRecent();
                        List<Foods> allFoods = foodsDB.getAllFoods(); // db là instance của DatabaseHelper
                        List<Foods> filtered = searchFoods(allFoods, keyword);
                        showSearchResult(filtered, "Kết quả cho \"" + keyword + "\"");
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }, 400);
                }
                return true;
            }
            return false;

        });
        searchRecent();
        searchChance();
        findViewById(R.id.btnExploreBestFood).setOnClickListener(v -> {
            List<Foods> bestFoods = foodsDB.getBestFoods();
            showSearchResult(bestFoods, "Món nổi bật");
        });
        back.setOnClickListener(view -> finish());
    }
    private List<Foods> searchFoods(List<Foods> foodList, String searchText) {
        List<Foods> result = new ArrayList<>();
        String keyword = removeDiacritics(searchText).toLowerCase();

        for (Foods item : foodList) {
            String title = removeDiacritics(item.getTitle()).toLowerCase();
            String categoryNormalized = removeDiacritics(dbHelper.getCategoryNameById(item.getCategoryId())).toLowerCase();
            if (title.contains(keyword) ||categoryNormalized.contains(keyword)) {
                result.add(item);
            }
        }

        return result;
    }
    private void showSearchResult(List<Foods> results, String titleIfAny) {
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
    private void searchRecent() {
        FlexboxLayout flexboxLayout = findViewById(R.id.recentKeywordsContainer);
        flexboxLayout.removeAllViews();

        List<String> recentKeywords =  SearchHistoryManager.getHistory(this);
        for (String keyword : recentKeywords) {
            TextView chip = new TextView(this);
            chip.setText(keyword);
            chip.setTextSize(14);
            chip.setPadding(24, 12, 24, 12);
            chip.setTextColor(Color.GRAY);
            chip.setBackgroundResource(R.drawable.bg_chip_keyword);

            chip.setOnClickListener(v -> {
                progressBar.setVisibility(View.VISIBLE);
                // Gán vào ô tìm kiếm
                searchEdt.setText(keyword);
                searchEdt.setSelection(keyword.length());

                recyclerView.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(() -> {
                    List<Foods> allFoods = foodsDB.getAllFoods(); // db là instance của DatabaseHelper
                    List<Foods> filtered = searchFoods(allFoods, keyword);
                    showSearchResult(filtered, "Kết quả cho \"" + keyword + "\"");
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }, 400);

            });

            flexboxLayout.addView(chip);
        }
    }

    private String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace("đ", "d")
                .replace("Đ", "D");
    }

    private void searchChance(){
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    if (s.toString().trim().isEmpty()) {
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        new Handler().postDelayed(() -> {
                            loadSuggestedFoods(); // Tải lại danh sách đề xuất
                            findViewById(R.id.layoutEmptyResult).setVisibility(View.GONE);
                            findViewById(R.id.txtRecent).setVisibility(View.VISIBLE);
                            findViewById(R.id.recentKeywordsContainer).setVisibility(View.VISIBLE);
                            findViewById(R.id.txtSuggestedTitle).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.txtSuggestedTitle)).setText("Được đề xuất");
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }, 400);

                    }
                }
            }
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
    }
    private void loadSuggestedFoods() {
        List<Foods> suggestedFoods = foodsDB.getSuggestedFoods();
            adapter.setData(suggestedFoods);
            adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }

}
