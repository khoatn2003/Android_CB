package com.example.app_fast_food.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.app_fast_food.Adapter.dinhDangTien;
import com.example.app_fast_food.Cart.CartActivity;
import com.example.app_fast_food.Helper.CartDatabase;
import com.example.app_fast_food.Cart.CartItem;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ActivityDetailFoodBinding;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class DetailFoodActivity extends AppCompatActivity {
    private Foods item; // Sản phẩm chi tiết
    private ActivityDetailFoodBinding binding;
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

        binding = ActivityDetailFoodBinding.inflate(getLayoutInflater());
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
        Log.d("DetailActivity", "User Logged In: " + isLoggedIn + ", Current User ID: " + currentUserId);
    }

    private void getBundleExtra() {
        item = (Foods) getIntent().getSerializableExtra("object");

        if (item == null) {
            Toast.makeText(this, "Không nhận được dữ liệu sản phẩm!", Toast.LENGTH_SHORT).show();
            Log.e("DetailActivity", "item is null. Intent data missing.");
            finish(); // Kết thúc Activity nếu không có dữ liệu
            return;
        }

        binding.titleTxt.setText(item.getTitle());
        binding.descriptionTxt.setText(item.getDescription());
        binding.priceTxt.setText(dinhDangTien.dinhdang(item.getPrice()));
        binding.rateTxt.setText(item.getStar() + " ");
        binding.totalTxt.setText(dinhDangTien.dinhdang(item.getPrice()));
        String imagePath = item.getImagePath(); //
        if (imagePath != null && !imagePath.isEmpty()) {
            int imageResourceId = getResources().getIdentifier(
                    imagePath,
                    "drawable",
                    getPackageName()
            );
            if (imageResourceId != 0) {
                binding.pic.setImageResource(imageResourceId);
            } else {
                Log.w("DetailActivity", "Khong tim thay anh: " + imagePath);
                binding.pic.setImageResource(R.drawable.jollibear_logo);
            }
        }
        binding.backBtn.setOnClickListener(v -> finish());

        binding.plusBtn.setOnClickListener(v -> {
            num = num + 1;
            binding.numTxt.setText(num + " ");
            binding.totalTxt.setText(dinhDangTien.dinhdang(num * item.getPrice()));

            CartItem cartItem = db.getCartByUser(currentUserId).getItemByFoodId(item.getId());
            updateButtonState(cartItem);
        });

        binding.minusBtn.setOnClickListener(v -> {
            if (num > 0) {
                num--;
                binding.numTxt.setText(String.valueOf(num));
                binding.totalTxt.setText(dinhDangTien.dinhdang(num * item.getPrice()));

                CartItem cartItem = db.getCartByUser(currentUserId).getItemByFoodId(item.getId());
                updateButtonState(cartItem);
            }
        });

        binding.pic.setOnClickListener(v -> {
            String imageUrl = item.getImagePath(); // nếu là URL hoặc path
            showImageOverlay(imageUrl);
        });

    }
    private void actionClick() {
        if (currentUserId == -1) {
            binding.addToCardBtn.setOnClickListener(view -> {
                Intent loginIntent = new Intent(DetailFoodActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                Toast.makeText(DetailFoodActivity.this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            });

        } else {
            binding.addToCardBtn.setOnClickListener(v -> {
                Log.d("MY_TAG", "Đã nhấn nút thêm giỏ hàng");
                if (item == null) {
                    Toast.makeText(DetailFoodActivity.this, "Lỗi sản phẩm, không thể thêm!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String note = binding.noteInput.getText().toString().trim();
                boolean success = db.addToCart(currentUserId, item, num, note);
                if (success) {
                    Toast.makeText(DetailFoodActivity.this, "\"" + item.getTitle() + "\" (x" + num + ") đã thêm vào giỏ!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailFoodActivity.this, CartActivity.class));
                } else {
                    Toast.makeText(DetailFoodActivity.this, "Lỗi khi thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            });

            binding.emptyBtn.setOnClickListener(view -> finish());
        }
        binding.buyBtn.setOnClickListener(v->{
            CartItem cartItem = new CartItem();
            cartItem.setFoodId(item.getId());
            cartItem.setFoodTitle(item.getTitle());
            cartItem.setFoodImagePath(item.getImagePath());
            cartItem.setPricePerItem(item.getPrice());
            cartItem.setQuantity(num);
            String note = binding.noteInput.getText().toString().trim();
            cartItem.setNote(note);

            ArrayList<CartItem> buyNowList = new ArrayList<>();
            buyNowList.add(cartItem);

            Intent intent = new Intent(this, ThanhToanActivity.class);
            intent.putExtra("cart_items", buyNowList);
            intent.putExtra("from_buy_now", true); // thêm cờ
            startActivity(intent);
        });

        binding.cartBtn.setOnClickListener(view -> startActivity(new Intent(DetailFoodActivity.this, CartActivity.class)));

    }

    private void updateButtonState(CartItem cartItem) {
        if (cartItem == null && num == 0) {
            binding.addToCardBtn.setVisibility(View.GONE);
            binding.buyBtn.setVisibility(View.GONE);
            binding.emptyBtn.setVisibility(View.VISIBLE);
        } else {
            // Hiện các nút thao tác lại
            binding.addToCardBtn.setVisibility(View.VISIBLE);
            binding.buyBtn.setVisibility(View.VISIBLE);
            binding.emptyBtn.setVisibility(View.GONE);

        }

    }
    private void showImageOverlay(String imageUrl) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_preview);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        PhotoView imgFull = dialog.findViewById(R.id.imgFull);
        ImageButton btnClose = dialog.findViewById(R.id.btnClose);

        // Load ảnh từ URL hoặc resource

        if (imageUrl != null && !imageUrl.isEmpty()) {
            int imageResourceId = getResources().getIdentifier(
                    imageUrl,
                    "drawable",
                    getPackageName()
            );
            if (imageResourceId != 0) {
                Glide.with(this).load(imageResourceId).into(imgFull);
            } else {
                Log.w("DetailActivity", "Khong tim thay anh: " + imageUrl);
                imgFull.setImageResource(R.drawable.jollibear_logo);
            }

            btnClose.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        }
        imgFull.setOnClickListener(v -> dialog.dismiss());
    }

}