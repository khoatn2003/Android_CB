package com.example.app_fast_food.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_fast_food.Adapter.dinhDangTien;
import com.example.app_fast_food.Cart.CartActivity;
import com.example.app_fast_food.Cart.CartDatabase;
import com.example.app_fast_food.Cart.CartItem;
import com.example.app_fast_food.Model.Cart;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ActivityEditFoodBinding;

public class EditFoodActivity extends AppCompatActivity {
    private Foods item; // Sản phẩm chi tiết
    private CartItem cartItem;
    private ActivityEditFoodBinding binding;
    private int num = 1;

    //---------
    private CartDatabase db;
    private int currentUserId = -1;

    // Sử dụng đúng tên SharedPreferences và Key bạn đã dùng khi lưu
    public static final String USER_SESSION_PREFS = "user_session";
    public static final String KEY_USER_ID_SESSION = "userId";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new CartDatabase(this);
        loadCurrentUserId();
        getBundleExtra();
        actionClick();

    }
    private void loadCurrentUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_SESSION_PREFS, Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            currentUserId = sharedPreferences.getInt(KEY_USER_ID_SESSION, -1);
        } else {
            currentUserId = -1;
        }
        Log.d("EditFood", "User Logged In: " + isLoggedIn + ", Current User ID: " + currentUserId);
    }

    private void getBundleExtra() {
        item = (Foods) getIntent().getSerializableExtra("food");
        cartItem = (CartItem)getIntent().getSerializableExtra("cart");
        if (item == null) {
            Toast.makeText(this, "Không nhận được dữ liệu sản phẩm!", Toast.LENGTH_SHORT).show();
            Log.e("EditFood", "item is null. Intent data missing.");
            finish(); // Kết thúc Activity nếu không có dữ liệu
            return;
        }

        // Hiển thị thông tin sản phẩm cơ bản
        binding.titleTxt.setText(item.getTitle());
        binding.descriptionTxt.setText(item.getDescription());
        binding.priceTxt.setText(dinhDangTien.dinhdang(item.getPrice()));
        binding.rateTxt.setText(item.getStar() + "");

        // Load ảnh sản phẩm
        String imagePath = item.getImagePath();
        int imageResId = (imagePath != null && !imagePath.isEmpty()) ?
                getResources().getIdentifier(imagePath, "drawable", getPackageName()) : 0;
        binding.pic.setImageResource(imageResId != 0 ? imageResId : R.drawable.jollibear_logo);

        // Xử lý dữ liệu giỏ hàng nếu có
        if (cartItem != null) {
            //num = Math.max(cartItem.getQuantity(), 1);
            num = cartItem.getQuantity() > 0 ? cartItem.getQuantity() : 1;
            binding.noteInput.setText(cartItem.getNote() != null ? cartItem.getNote() : "");
        } else {
            num = 1;
            binding.noteInput.setText("");
        }

        binding.numTxt.setText(String.valueOf(num));
        updateButtonState(cartItem);

        binding.plusBtn.setOnClickListener(v -> {
            num++;
            binding.numTxt.setText(String.valueOf(num));
            updateButtonState(cartItem);
        });

        binding.minusBtn.setOnClickListener(v -> {
            if (num > 0) {
                num--;
                binding.numTxt.setText(String.valueOf(num));
                updateButtonState(cartItem);
            }
        });

        binding.backBtn.setOnClickListener(v -> finish());
    }
    private void actionClick() {
        int cartId = cartItem.getCartId();
        if (currentUserId == -1) {
            binding.addToCardBtn.setOnClickListener(view -> {
                Intent loginIntent = new Intent(EditFoodActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                Toast.makeText(EditFoodActivity.this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            });

        } else {
            binding.updateBtn.setOnClickListener(v->{
                String note = binding.noteInput.getText().toString().trim();
                int currentQuantity = db.getQuantityInCart(currentUserId, item.getId(),note);

                boolean success = db.updateToCartById(cartId, currentUserId, item.getId(), currentQuantity, num, note);
                if (success) {
                    Toast.makeText(EditFoodActivity.this, "Cập nhật\"" + item.getTitle() + "\" (x" + num + ") thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditFoodActivity.this, CartActivity.class));
                } else {
                    Toast.makeText(EditFoodActivity.this, "Lỗi khi cập nhật giỏ hàng!", Toast.LENGTH_SHORT).show();
                }

            });

            binding.deleteBtn.setOnClickListener(v->{
                String note = binding.noteInput.getText().toString().trim();
                int currentQuantity = db.getQuantityInCart(currentUserId, item.getId(),note);

                boolean success = db.updateToCartById(cartId, currentUserId, item.getId(), currentQuantity, num, note);
                if (success) {
                    Toast.makeText(EditFoodActivity.this, "Đã xóa\"" + item.getTitle() + "\"ra khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditFoodActivity.this, CartActivity.class));
                } else {
                    Toast.makeText(EditFoodActivity.this, "Lỗi khi xóa giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            });

            binding.emptyBtn.setOnClickListener(view -> finish());
        }

        binding.cartBtn.setOnClickListener(view -> startActivity(new Intent(EditFoodActivity.this, CartActivity.class)));
    }

    private void updateButtonState(CartItem cartItem) {
        if (cartItem == null && num == 0) {
            // Sản phẩm chưa có trong giỏ, số lượng = 0 → chỉ hiện nút quay lại
            binding.actionButtonContainer.setVisibility(View.GONE);
            binding.emptyBtn.setVisibility(View.VISIBLE);
        } else {
            // Hiện các nút thao tác lại
            binding.actionButtonContainer.setVisibility(View.VISIBLE);
            binding.emptyBtn.setVisibility(View.GONE);

            if (cartItem == null && num > 0) {
                // Sản phẩm chưa có trong giỏ, số lượng > 0 → thêm mới
                binding.addToCardBtn.setVisibility(View.VISIBLE);
                binding.updateBtn.setVisibility(View.GONE);
                binding.deleteBtn.setVisibility(View.GONE);
            } else if (cartItem != null && num == 0) {
                // Sản phẩm đã có, nhưng giảm về 0 → hiện xóa
                binding.addToCardBtn.setVisibility(View.GONE);
                binding.updateBtn.setVisibility(View.GONE);
                binding.deleteBtn.setVisibility(View.VISIBLE);
            } else {
                // Sản phẩm đã có, số lượng > 0 → cập nhật
                binding.addToCardBtn.setVisibility(View.GONE);
                binding.updateBtn.setVisibility(View.VISIBLE);
                binding.deleteBtn.setVisibility(View.GONE);

                String buttonText = "Cập nhật giỏ hàng - " +dinhDangTien.dinhdang(num *item.getPrice());
                binding.updateBtn.setText(buttonText);
            }
        }
    }


}