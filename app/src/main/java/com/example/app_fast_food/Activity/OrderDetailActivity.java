package com.example.app_fast_food.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.app_fast_food.Adapter.dinhDangTien;
import com.example.app_fast_food.Cart.CartItem;
import com.example.app_fast_food.Helper.OrderDatabase;
import com.example.app_fast_food.Order.OrderItemDisplay;
import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ActivityOrderDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    ActivityOrderDetailBinding binding;
    OrderItemDisplay itemOrder;
    OrderDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemOrder = (OrderItemDisplay) getIntent().getSerializableExtra("orderItem");
        String imagePath = itemOrder.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            int imageResourceId = getResources().getIdentifier(
                    imagePath,
                    "drawable",
                    getPackageName()
            );
            if (imageResourceId != 0) {
                binding.imgFood.setImageResource(imageResourceId);
            } else {
                Log.w("OrderDetail", "Khong tim thay anh: " + imagePath);
                binding.imgFood.setImageResource(R.drawable.jollibear_logo);
            }
        }
        binding.maDonhang.setText(String.valueOf(itemOrder.getOrderID()));
        binding.txtTitleOrder.setText(itemOrder.getFoodTitle());
        binding.txtPrice.setText(dinhDangTien.dinhdang(itemOrder.getPricePerItem()));
        binding.txtQuantity.setText(String.valueOf(itemOrder.getQuantity()));
        binding.txtPaymentTitle.setText(itemOrder.getPaymentMethod());
        binding.txtOrderDate.setText(itemOrder.getOrderDateItem());
        binding.txtAddress.setText(itemOrder.getAddressOrder());
        String note = itemOrder.getNote();
        if(note.isEmpty()){
            binding.txtNote.setText("Không có");
        }else{binding.txtNote.setText(note);}
        binding.txtTotal.setText(dinhDangTien.dinhdang(itemOrder.getPricePerItem() * itemOrder.getQuantity()));
        binding.btnBack.setOnClickListener(view -> finish());
        binding.btnHuy.setOnClickListener(view -> HuyDon());
        binding.btnReOrder.setOnClickListener(view -> ReOrder() );
        updateThoiGianDonHang();
        updateTrangThaiDonHang();
    }

    private void HuyDon(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cancel_reason, null);
        bottomSheetDialog.setContentView(view);

        RadioGroup rgReasons = view.findViewById(R.id.rgReasons);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(view12 -> {
            int checkedId = rgReasons.getCheckedRadioButtonId();
            if (checkedId != -1) {
                RadioButton selected = view.findViewById(checkedId);
                //String reason = selected.getText().toString();
                db = new OrderDatabase(this);
                boolean success = db.cancelOrderItem(itemOrder.getOrderItemID());
                if (success) {
                    //Toast.makeText(context, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                    itemOrder.setStatus("Đã hủy");
                    bottomSheetDialog.dismiss();

                    Intent resultIntent = new Intent();
                    String currentStatus = getIntent().getStringExtra("current_status");
                    resultIntent.putExtra("order_cancelled", true);
                    resultIntent.putExtra("previous_status", currentStatus);

                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Không thể hủy đơn này", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            }else {
                Toast.makeText(this, "Vui lòng chọn lý do hủy", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(view1 -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }
    private void ReOrder(){
        List<CartItem> buyAgainList = new ArrayList<>();
        CartItem cartItem = new CartItem();
        cartItem.setFoodId(itemOrder.getFoodID());
        cartItem.setFoodTitle(itemOrder.getFoodTitle());
        cartItem.setFoodImagePath(itemOrder.getImagePath());
        cartItem.setPricePerItem(itemOrder.getPricePerItem());
        cartItem.setQuantity(itemOrder.getQuantity());
        cartItem.setNote(itemOrder.getNote());

        buyAgainList.add(cartItem);
        Intent intent = new Intent(this, ThanhToanActivity.class);
        intent.putExtra("cart_items", (Serializable) buyAgainList);
        intent.putExtra("from_buy_now", true);
        startActivity(intent);
    }

    public void updateThoiGianDonHang(){
        binding.txtDeliveryDate.setText(itemOrder.getOrderShippingTime());
        binding.txtReceiveDate.setText(itemOrder.getOrderReceiveTime());
        binding.txtCancelDate.setText(itemOrder.getOrderCancelTime());
    }
    private void updateTrangThaiDonHang(){
        String status = itemOrder.getStatus();

        if (status != null) {
            String displayText = "";
            switch (status) {
                case "Chờ xác nhận":
                    binding.statusFrame.setVisibility(View.VISIBLE);
                    binding.TTvanchuyen.setVisibility(View.GONE);
                    binding.layoutDeliveryDate.setVisibility(View.GONE);
                    binding.layoutReceiveDate.setVisibility(View.GONE);
                    binding.layoutCancelDate.setVisibility(View.GONE);
                    binding.view6.setVisibility(View.GONE);
                    binding.view7.setVisibility(View.GONE);
                    displayText = "Đơn hàng đang chờ xác nhận";
                    break;
                case "Chờ lấy hàng":
                    binding.statusFrame.setVisibility(View.VISIBLE);
                    binding.TTvanchuyen.setVisibility(View.GONE);
                    binding.layoutDeliveryDate.setVisibility(View.GONE);
                    binding.layoutReceiveDate.setVisibility(View.GONE);
                    binding.layoutCancelDate.setVisibility(View.GONE);
                    binding.view6.setVisibility(View.GONE);
                    binding.view7.setVisibility(View.GONE);
                    displayText = "Đơn hàng đang được chuẩn bị";
                    break;
                case "Đang giao":
                    binding.statusFrame.setVisibility(View.GONE);
                    binding.TTvanchuyen.setVisibility(View.VISIBLE);
                    binding.layoutDeliveryDate.setVisibility(View.VISIBLE);
                    binding.layoutReceiveDate.setVisibility(View.GONE);
                    binding.layoutCancelDate.setVisibility(View.GONE);
                    binding.view6.setVisibility(View.VISIBLE);
                    binding.view7.setVisibility(View.GONE);
                    break;
                case "Đã giao":
                    binding.statusFrame.setVisibility(View.VISIBLE);
                    binding.TTvanchuyen.setVisibility(View.GONE);
                    binding.layoutDeliveryDate.setVisibility(View.VISIBLE);
                    binding.layoutReceiveDate.setVisibility(View.VISIBLE);
                    binding.layoutCancelDate.setVisibility(View.GONE);
                    binding.btnHuy.setVisibility(View.GONE);
                    binding.btnReOrder.setVisibility(View.VISIBLE);
                    binding.view6.setVisibility(View.VISIBLE);
                    binding.view7.setVisibility(View.GONE);
                    displayText = "Đơn hàng đã được giao thành công";
                    break;
                case "Đã hủy":
                    binding.statusFrame.setVisibility(View.VISIBLE);
                    binding.TTvanchuyen.setVisibility(View.GONE);
                    binding.layoutDeliveryDate.setVisibility(View.GONE);
                    binding.layoutReceiveDate.setVisibility(View.GONE);
                    binding.layoutCancelDate.setVisibility(View.VISIBLE);
                    binding.btnHuy.setVisibility(View.GONE);
                    binding.btnReOrder.setVisibility(View.VISIBLE);
                    binding.view6.setVisibility(View.GONE);
                    binding.view7.setVisibility(View.VISIBLE);
                    displayText = "Đơn hàng đã bị hủy";
                    break;
                default:
                    binding.statusFrame.setVisibility(View.VISIBLE);
                    binding.TTvanchuyen.setVisibility(View.GONE);
                    displayText = "Trạng thái không xác định";
                    break;
            }
            binding.trangthai.setText(displayText);
        }

    }
}