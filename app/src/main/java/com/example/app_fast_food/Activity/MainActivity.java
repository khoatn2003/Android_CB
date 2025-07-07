package com.example.app_fast_food.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_fast_food.Adapter.BannerAdapter;
import com.example.app_fast_food.Adapter.BestFoodAdapter;
import com.example.app_fast_food.Adapter.CategoryApdater;
import com.example.app_fast_food.Cart.CartActivity;
import com.example.app_fast_food.Helper.DatabaseHelper;
import com.example.app_fast_food.Helper.FoodsDatabase;
import com.example.app_fast_food.Model.Category;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ActivityMainBinding;
import com.example.app_fast_food.databinding.ViewholderCategoryBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private FoodsDatabase foodDB;
    private ActivityMainBinding binding;
    CategoryApdater adapter;
    private BestFoodAdapter bestFoodAdapter;
    private List<Foods> bestFoodList;

    private ViewPager2 bannerViewPager;
    private TabLayout tabDots;
    private Handler handler = new Handler();
    private int currentPage = 0;
    private List<Integer> bannerImages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBanner();
        initCategory();

        setVariable();

        checkListFood();
        initBestFood();
        loadBestFoods();

        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String  userName = sp.getString("email", "");
            String phone = sp.getString("phone", "");
            // Bạn có thể sử dụng userId, phone để lấy thêm dữ liệu nếu cần
            binding.txtUser.setText(userName);
        }
    }

    private void setVariable() {
        binding.logoutBtn.setOnClickListener(v->{
            SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // hoặc editor.remove("isLoggedIn");
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            Toast.makeText(this,"Bạn đã đăng xuất tài khoản",Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();

        });
        binding.cartBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
        binding.btninfo.setOnClickListener(view -> {
            SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
            boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);
            if (isLoggedIn) {
                Toast.makeText(this,"Sắp ra mắt nè", Toast.LENGTH_SHORT).show();
            }else{
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }

        });

        binding.btndonhang.setOnClickListener(view -> {
            startActivity(new Intent(this, OrderActivity.class));
        });

        binding.btnfav.setOnClickListener(view -> {
            Toast.makeText(this,"Chưa có trang này", Toast.LENGTH_SHORT).show();
        });
        binding.btnCartBottom.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, CartActivity.class)));

        binding.searchEdt.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        binding.searchEdt.setFocusable(false); // Ngăn hiện bàn phím ở Main


    }

    private void initBanner() {
        bannerViewPager = findViewById(R.id.bannerViewPager);
        tabDots = findViewById(R.id.tabDots);

        bannerImages = Arrays.asList(
                R.drawable.banner2,
                R.drawable.banner,
                R.drawable.banner1,
                R.drawable.banner3
        );
        BannerAdapter adapter = new BannerAdapter(this,bannerImages);
        bannerViewPager.setAdapter(adapter);

        // Gắn TabLayout với ViewPager2
        new TabLayoutMediator(tabDots, bannerViewPager,
                (tab, position) -> {
                    // Không cần xử lý gì thêm nếu chỉ hiện dot
                }).attach();
        //Tự động chuyển ảnh
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPage = (currentPage + 1) % bannerImages.size();
                bannerViewPager.setCurrentItem(currentPage, true);
                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void initBestFood(){
        // Khởi tạo danh sách (ban đầu có thể rỗng)
        bestFoodList = new ArrayList<>();
        bestFoodAdapter = new BestFoodAdapter(this, bestFoodList);

        // RecyclerView bestFoodView = findViewById(R.id.bestFoodView); // Nếu không dùng binding trực tiếp
        binding.bestFoodView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.bestFoodView.setAdapter(bestFoodAdapter);
        Log.d("MainActivity", "BestFood RecyclerView setup complete.");
    }

    private void loadBestFoods() {
        foodDB = new FoodsDatabase(this);
        // Lấy danh sách best foods từ database
        List<Foods> foodsFromDb = foodDB.getBestFoods();
        if (foodsFromDb != null && !foodsFromDb.isEmpty()) {
            // Cập nhật dữ liệu cho adapter
            // Cách 1: Nếu adapter có phương thức updateData
            binding.progressBarBestFood.setVisibility(View.VISIBLE);
            bestFoodAdapter.updateData(foodsFromDb);

            Log.d("MainActivity", "Loaded " + foodsFromDb.size() + " best food items.");
        } else {
            Log.d("MainActivity", "No best food items found in the database.");
            // Có thể hiển thị thông báo cho người dùng nếu không có dữ liệu
        }
        binding.progressBarBestFood.setVisibility(View.GONE);
    }

    private void initCategory(){
        db = new DatabaseHelper(this);

        // Chỉ thêm dữ liệu nếu database rỗng
        if (db.getAllCategories().isEmpty()) {
            insertInitialData();
        }

        // 1. Khởi tạo adapter với list rỗng
        adapter = new CategoryApdater(new ArrayList<>(), this);
        binding.categoryView.setLayoutManager(new GridLayoutManager(this, 4)); // hoặc LinearLayoutManager nếu bạn muốn
        binding.categoryView.setAdapter(adapter); // Gắn sớm adapter để tránh lỗi log

        // 2. Nạp dữ liệu thật và cập nhật adapter
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        List<Category> list = db.getAllCategories();
        if (!list.isEmpty()) {
            adapter.setCategoryList(list); // gọi hàm mới để cập nhật
            adapter.notifyDataSetChanged(); // báo cho adapter biết dữ liệu thay đổi
        }
        binding.progressBarCategory.setVisibility(View.GONE);
    }

    private void insertInitialData() {
        db.insertCategory(new Category(0, "btn_1", "Pizza"));
        db.insertCategory(new Category(1, "btn_2", "Burger"));
        db.insertCategory(new Category(2, "btn_3", "Chicken"));
        db.insertCategory(new Category(3, "btn_4", "Sushi"));
        db.insertCategory(new Category(4, "btn_5", "Meat"));
        db.insertCategory(new Category(5, "btn_6", "Hotdog"));
        db.insertCategory(new Category(6, "btn_7", "Drink"));
        db.insertCategory(new Category(7, "btn_8", "More"));
    }

    private void checkListFood(){
        foodDB = new FoodsDatabase(this);
        if (foodDB.getAllFoods().isEmpty()) {
            insertInitialDataFoods();
        }
    }
    private void insertInitialDataFoods() {
        foodDB = new FoodsDatabase(this);
        //Pizza
        foodDB.addFood(new Foods(0, "Margherita","Pizza phô mai được làm từ Ý",250000,"margherita",0,4.5,15,false));
        foodDB.addFood(new Foods(1, "Veggie Supreme","Veggie Supreme được làm từ Ý",250000,"pizza7",0,4.5,15,false));
        foodDB.addFood(new Foods(2, "Mediterranean Joy","Mediterranean Joy",250000,"pizza1",0,4.5,15,false));
        foodDB.addFood(new Foods(3, "Meat Feast pizza","Pizza phô mai thịt siêu hấp dẫn",250000,"pizza6",0,4.5,15,true));
        foodDB.addFood(new Foods(4, "Hawaiian Paradise","Pizza phô mai được làm từ Ý",250000,"pizza4",0,4.5,15,false));
        foodDB.addFood(new Foods(5, "Pepperoni Lovers","Pizza phô mai được làm từ Ý",250000,"pizza5",0,4.5,15,false));

        //Burger
        foodDB.addFood(new Foods(6, "Classic Beef Burger","Ăn là mê",25000,"burger1",1,4.5,15,false));
        foodDB.addFood(new Foods(7, "Bacon and Cheese Heaven","Bacon and Cheese Heaven độc quyền Jollibear",250000,"burger2",1,4.5,15,true));
        foodDB.addFood(new Foods(8, "BBQ Ranch Delight","BBQ Ranch Delight độc quyền Jollibear",30000,"burger3",1,4.5,10,false));
        foodDB.addFood(new Foods(9, "Teriyaki Pineapple Pleasure","Teriyaki Pineapple Pleasure độc quyền Jollibear",25000,"burger4",1,4.5,15,false));
        foodDB.addFood(new Foods(10, "Spicy Jalapeño Burger","Spicy Jalapeño Burger độc quyền Jollibear",30000,"burger5",1,4.5,10,false));

        //Chicken
        foodDB.addFood(new Foods(11, "Gà Nướng Mật Ong", "Đùi gà tơ nướng mật ong rừng, da vàng óng, thịt mềm thơm.", 280000, "chicken1", 2, 4.6, 25, false));
        foodDB.addFood(new Foods(12, "Gà Xào Sả Ớt", "Thịt gà ta xào săn cùng sả và ớt chuông, vị cay nồng ấm áp.", 260000, "chicken2", 2, 4.7, 20, true)); // BestFood = true
        foodDB.addFood(new Foods(13, "Salad Ức Gà Áp Chảo", "Ức gà tươi áp chảo vừa chín tới, trộn cùng rau củ và sốt mè rang.", 290000, "chicken3", 2, 4.4, 15, false));
        foodDB.addFood(new Foods(14, "Gà Luộc Lá Chanh", "Gà ta luộc truyền thống, da vàng ươm, thịt ngọt, chấm muối tiêu chanh.", 250000, "chicken6", 2, 4.5, 30, false));

        //Sushi
        foodDB.addFood(new Foods(15, "Set Sushi Cá Hồi Đặc Biệt", "Set sushi cá hồi tươi ngon gồm nigiri, sashimi và maki cá hồi.", 280000, "sushi", 3, 4.9, 15, true)); // BestFood = true
        foodDB.addFood(new Foods(16, "Sushi Cuộn California", "Sushi cuộn kiểu California với thanh cua, bơ, dưa chuột và trứng cá.", 250000, "sushi1", 3, 4.7, 10, false));
        foodDB.addFood(new Foods(17, "Combo Sushi Lươn Nhật", "Combo sushi lươn nướng unagi đậm đà, kèm súp miso.", 295000, "sushi6", 3, 4.8, 20, false));

        //Meat
        foodDB.addFood(new Foods(18, "Bò Bít Tết Sốt Tiêu Xanh", "Thăn bò Úc mềm ngọt, áp chảo hoàn hảo, dùng kèm sốt tiêu xanh đậm đà.", 350000, "meat2", 4, 4.8, 25, false)); // BestFood = true
        foodDB.addFood(new Foods(19, "Sườn Cừu Nướng Thảo Mộc", "Sườn cừu non tẩm ướp thảo mộc, nướng thơm lừng, giữ trọn vị ngọt tự nhiên.", 420000, "meat5", 4, 4.9, 30, false));
        foodDB.addFood(new Foods(20, "Thịt Heo Quay Giòn Bì", "Ba chỉ heo quay với lớp bì giòn tan, thịt bên trong mềm mọng, chấm nước tương tỏi ớt.", 180000, "meat6", 4, 4.7, 40, false));

        //Hotdog
        foodDB.addFood(new Foods(21, "Hotdog Truyền Thống", "Xúc xích Đức nướng thơm, kẹp bánh mì mềm, thêm mù tạt vàng và tương cà.", 20000, "hotdog1", 5, 4.3, 5, false));
        foodDB.addFood(new Foods(22, "Hotdog Phô Mai Kéo Sợi", "Hotdog bọc phô mai mozzarella, chiên giòn, kéo sợi hấp dẫn.", 28000, "hotdog3", 5, 4.6, 10, false)); // BestFood = true
        foodDB.addFood(new Foods(23, "Hotdog Xúc Xích Cay", "Xúc xích cay nồng kẹp bánh mì, thêm hành tây và sốt Sriracha.", 25000, "hotdog4", 5, 4.4, 7, false));
        foodDB.addFood(new Foods(24, "Hotdog Bò Bằm Kiểu Mỹ", "Xúc xích bò bằm, kẹp bánh mì, thêm dưa chuột muối và sốt relish.", 30000, "hotdog7", 5, 4.5, 12, false));

        //Drink
        foodDB.addFood(new Foods(25, "Nước Cam Ép Tươi", "Nước cam tươi 100% nguyên chất, giàu vitamin C.", 35000, "drink", 6, 4.7, 3, false)); // BestFood = true
        foodDB.addFood(new Foods(26, "Trà Chanh Giã Tay", "Trà chanh truyền thống, vị chua ngọt thanh mát.", 25000, "drink1", 6, 4.5, 5, false));
        foodDB.addFood(new Foods(27, "Cà Phê Sữa Đá", "Cà phê phin đậm đà kết hợp cùng sữa đặc béo ngậy.", 30000, "drink2", 6, 4.6, 5, false));
        foodDB.addFood(new Foods(28, "Sinh Tố Xoài", "Sinh tố xoài cát chín cây, sánh mịn, ngọt thơm.", 45000, "drink3", 6, 4.8, 5, false));
        foodDB.addFood(new Foods(29, "Nước Suối Tinh Khiết", "Nước suối đóng chai, tinh khiết và mát lạnh.", 15000, "drink4", 6, 4.2, 2, false));

        //More
        foodDB.addFood(new Foods(30, "Combo Gia Đình Vui Vẻ", "1 Pizza cỡ lớn, 2 Burger, 4 Gà rán, 1 Coca lớn.", 399000, "more1", 7, 4.7, 25, false)); // BestFood = true
        foodDB.addFood(new Foods(31, "Salad Trộn Dầu Giấm", "Khoai tây cắt sợi chiên vàng giòn, rắc muối tiêu.", 35000, "more2", 7, 4.4, 10, false));
        foodDB.addFood(new Foods(32, "Burger chay", "Salad rau củ tươi ngon trộn với sốt dầu giấm chua ngọt.", 55000, "more3", 7, 4.5, 10, false));
        foodDB.addFood(new Foods(33, "Bánh mì kẹp hotdog", "Bánh mỳ kẹp hotdog với sốt cà chua bò bằm truyền thống.", 95000, "more4", 7, 4.6, 20, false));
        foodDB.addFood(new Foods(34, "Phomat", "Phomat tơi xốp cùng tôm, mực và rau củ.", 80000, "more5", 7, 4.5, 15, false));
        foodDB.addFood(new Foods(35, "Set Khai Vị Tổng Hợp", "Mỳ Ý, chả giò và nem nướng cho nhóm bạn.", 150000, "more8", 7, 4.6, 20, false));
    }

    @Override
    protected void onDestroy() {
        db.close(); // đúng chỗ
        super.onDestroy();
    }
}


