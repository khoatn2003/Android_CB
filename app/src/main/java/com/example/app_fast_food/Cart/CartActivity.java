package com.example.app_fast_food.Cart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_fast_food.Activity.MainActivity;
import com.example.app_fast_food.Activity.ThanhToanActivity;
import com.example.app_fast_food.Adapter.dinhDangTien;
import com.example.app_fast_food.Helper.DatabaseHelper;
import com.example.app_fast_food.Model.Cart;
import com.example.app_fast_food.databinding.ActivityCartBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private CartDatabase dbCart;
    private int currentUserId = -1;

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
        
        binding.backBtn.setOnClickListener(view -> startActivity(new Intent(CartActivity.this, MainActivity.class)));
        binding.thanhToanbtn.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, ThanhToanActivity.class);
            intent.putExtra("cart_items", (Serializable) cartItemList); // truyền danh sách
            startActivity(intent);
        });
        binding.voucherLayout.setOnClickListener(view -> Toast.makeText(this,"Chuẩn bị ra mắt trang này", Toast.LENGTH_SHORT).show());
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
                updateCartUIVisibility();
                KiemTraCart();
            }
        });
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(cartAdapter);
    }
    private void loadCartItems() {
        if (currentUserId != -1) {
            Cart cart = dbCart.getCartByUser(currentUserId);
            List<CartItem> itemsFromDb = cart.getItems();
            cartItemList.clear(); // Xóa dữ liệu cũ trước khi thêm mới
            if (itemsFromDb != null && !itemsFromDb.isEmpty()) {
                cartAdapter.updateData(itemsFromDb);
                calculateAndDisplayTotals();

            } else {
                cartAdapter.updateData(new ArrayList<>());
            }
            Log.d("CartActivity", "Có " + cartItemList.size() + " sản phẩm");
        } else {
            // Xử lý trường hợp không có userId (đã xử lý ở onCreate, nhưng để an toàn)
            cartAdapter.updateData(new ArrayList<>());
        }
    }

    private void updateCartUIVisibility() {
        if (cartAdapter.getItemCount() == 0) {
            binding.cartView.setVisibility(View.GONE);
            binding.layoutTotal.setVisibility(View.GONE);
        } else {
            binding.cartView.setVisibility(View.VISIBLE);
            binding.layoutTotal.setVisibility(View.VISIBLE);
            calculateAndDisplayTotals(); // Tính toán và hiển thị tổng tiền
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