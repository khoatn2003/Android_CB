package com.example.app_fast_food.Cart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_fast_food.Activity.EditFoodActivity;
import com.example.app_fast_food.Activity.MainActivity;
import com.example.app_fast_food.Activity.ThanhToanActivity;
import com.example.app_fast_food.Adapter.dinhDangTien;
import com.example.app_fast_food.Helper.CartDatabase;
import com.example.app_fast_food.Helper.DatabaseHelper;
import com.example.app_fast_food.Helper.FoodsDatabase;
import com.example.app_fast_food.Model.Cart;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.databinding.ActivityCartBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private DatabaseHelper db;
    private CartDatabase dbCart;
    private FoodsDatabase foodDB;
    private int currentUserId = -1;
    private ActivityResultLauncher<Intent> editItemLauncher;

    private static final int REQUEST_EDIT_CART_ITEM = 1001;
    private static final int REQUEST_THANH_TOAN = 1402;

    // Sử dụng đúng tên SharedPreferences và Key bạn đã dùng khi lưu
    public static final String USER_SESSION_PREFS = "user_session";
    public static final String KEY_USER_ID_SESSION = "userId";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private double saleFee = 0; // Ví dụ: Phí vận chuyển cố định (bạn có thể làm phức tạp hơn)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbCart = new CartDatabase(this);
        loadCurrentUserId();
        if (currentUserId == -1) {
            // Người dùng chưa đăng nhập, xử lý tương ứng
            Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setupRecyclerView();
        KiemTraCart();
        loadCartItems();

        binding.backBtn.setOnClickListener(view -> finish());
        binding.thanhToanbtn.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, ThanhToanActivity.class);
            intent.putExtra("cart_items", (Serializable) cartItemList);
            startActivityForResult(intent, REQUEST_THANH_TOAN);

        });
        binding.voucherLayout.setOnClickListener(view -> Toast.makeText(this,"Bạn chưa có voucher nào", Toast.LENGTH_SHORT).show());
        cartAdapter.setOnEditItemClickListener(position -> {
            foodDB = new FoodsDatabase(this);
            CartItem cartItem = cartItemList.get(position);
            Foods food = foodDB.getFoodById(cartItem.getFoodId());
            if (food != null) {
                Intent intent = new Intent(this, EditFoodActivity.class);
                intent.putExtra("food", food);
                intent.putExtra("cart", cartItem);
                //editItemLauncher.launch(intent);
                startActivityForResult(intent, REQUEST_EDIT_CART_ITEM);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems(); // Load lại từ SQLite
        KiemTraCart();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CartActivityRESULT", "onActivityResult được gọi");
        if (requestCode == REQUEST_EDIT_CART_ITEM && resultCode == RESULT_OK && data != null) {
            Log.d("CartActivityINTENT", "Dữ liệu trả về thành công");
            CartItem updatedItem = (CartItem) data.getSerializableExtra("updatedCartItem");
            Log.d("CartActivityID", "CartID = " + updatedItem.getCartId() + " | New Qty = " + updatedItem.getQuantity());

            loadCartItems();
            KiemTraCart();
        }

    }

    private void loadCurrentUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_SESSION_PREFS, Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        if (isLoggedIn) {
            currentUserId = sharedPreferences.getInt(KEY_USER_ID_SESSION, -1);
        } else {
            currentUserId = -1;
        }
        Log.d("CartActivity", "User Logged In: " + isLoggedIn + ", Current User ID: " + currentUserId);
    }

    private void setupRecyclerView() {
        cartItemList = new ArrayList<>();
        // Truyền 'this' (CartActivity) làm listener cho adapter
        cartAdapter = new CartAdapter(this, cartItemList, currentUserId, new CartAdapter.CartInteractionListener() {
            @Override
            public void onCartItemChanged() {
                calculateAndDisplayTotals();
                KiemTraCart();
            }
        });
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(cartAdapter);
    }
    private void loadCartItems() {
        if (currentUserId != -1) {
            dbCart = new CartDatabase(this);
            Cart cart = dbCart.getCartByUser(currentUserId);
            List<CartItem> itemsFromDb = cart.getItems();
            cartItemList.clear(); // Xóa dữ liệu cũ trước khi thêm mới
            Log.d("CartActivityTest", "Trước khi clear: cartItemList size = " + cartItemList.size());
            if (itemsFromDb != null && !itemsFromDb.isEmpty()) {
                cartAdapter.updateData(itemsFromDb);
                Log.d("CartActivityTest", "Sau updateData: cartItemList size = " + cartItemList.size());
            } else {
                cartAdapter.updateData(new ArrayList<>());
            }
            calculateAndDisplayTotals();
        } else {
            cartAdapter.updateData(new ArrayList<>());
        }
    }
    private void calculateAndDisplayTotals() {
        double subtotal = 0;
        // Lấy danh sách item hiện tại từ adapter để tính toán
        // Bạn cần thêm phương thức này vào CartAdapter để lấy danh sách item hiện tại của nó

        for (CartItem item : cartItemList) {
            subtotal += item.getToTalPrice();
        }
        //double tax = subtotal * taxRate;
        double currentSaleFee = saleFee; // Luôn tính phí giảm giá
        double tienSale =subtotal*currentSaleFee;
        double totalSaleFee = subtotal - (tienSale);

        binding.saveAmount.setText(dinhDangTien.dinhdang(tienSale));
        binding.totalTxt.setText(dinhDangTien.dinhdang(totalSaleFee));
    }

    private void KiemTraCart(){
        Cart cart = dbCart.getCartByUser(currentUserId);
        List<CartItem> cartItems = cart.getItems();

        if (cartItems == null || cartItems.isEmpty()) {
            // Không có sản phẩm nào trong giỏ hàng -> chuyển về trang giỏ trống
            binding.cartView.setVisibility(View.GONE);
            binding.layoutTotal.setVisibility(View.GONE);
            binding.emptyCartLayout.setVisibility(View.VISIBLE);
            binding.button2.setOnClickListener(view -> startActivity(new Intent(CartActivity.this, MainActivity.class)));
        }else{
            binding.cartView.setVisibility(View.VISIBLE);
            binding.emptyCartLayout.setVisibility(View.GONE);
        }

    }
}

       /* editItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        CartItem updatedItem = (CartItem) result.getData().getSerializableExtra("updatedCartItem");

                        Log.d("CartActivityINTENT", "onActivityResult - Updated ID: " + updatedItem.getCartId());

                        loadCartItems(); // Load lại giỏ hàng từ DB
                    }
                }
        );*/