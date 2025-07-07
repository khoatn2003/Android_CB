package com.example.app_fast_food.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_fast_food.Activity.OrderActivity;
import com.example.app_fast_food.Activity.OrderDetailActivity;
import com.example.app_fast_food.Activity.ThanhToanActivity;
import com.example.app_fast_food.Cart.CartAdapter;
import com.example.app_fast_food.Cart.CartItem;
import com.example.app_fast_food.Helper.OrderDatabase;
import com.example.app_fast_food.Order.OrderItem;
import com.example.app_fast_food.Order.OrderItemDisplay;
import com.example.app_fast_food.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final Context context;
    private List<OrderItemDisplay> orders = new ArrayList<>();
    private  final OrderDatabase db;
    private OnOrderChangedListener orderChangedListener;

    public interface OnOrderChangedListener {
        void onOrderChanged();
    }
    public void setOnOrderChangedListener(OnOrderChangedListener listener) {
        this.orderChangedListener = listener;
    }
    public OrderAdapter(Context context, List<OrderItemDisplay> orders) {
        this.context = context;
        this.orders = orders;
        this.db = new OrderDatabase(context);
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent,false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        OrderItemDisplay item = orders.get(position);
        holder.txtOrderName.setText(item.getFoodTitle());
        holder.txtOrderStatus.setText(item.getStatus());
        holder.total_quantity_text_view.setText(context.getString(R.string.product_quantity, item.getQuantity()));
        holder.total_price_text_view.setText(dinhDangTien.dinhdang(item.getQuantity() * item.getPricePerItem()));
        int imageResId = context.getResources().getIdentifier(
                item.getImagePath(), "drawable", context.getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(imageResId)
                .placeholder(R.drawable.jollibear_logo)
                .error(R.drawable.jollibear_logo)
                .into(holder.imgOrder);

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderItem", item);
            intent.putExtra("current_status", item.getStatus());
            ((Activity) context).startActivityForResult(intent, OrderActivity.REQUEST_ORDER_DETAIL);
        });

        holder.btnHuyDon.setOnClickListener(v->{
            if(item.getStatus().equals("Đang giao")) {
                Toast.makeText(context, "Đơn hàng đã xác nhận không thể hủy", Toast.LENGTH_SHORT).show();
            }else {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_cancel_reason, null);
                bottomSheetDialog.setContentView(view);

                RadioGroup rgReasons = view.findViewById(R.id.rgReasons);
                Button btnCancel = view.findViewById(R.id.btnCancel);
                Button btnConfirm = view.findViewById(R.id.btnConfirm);
                btnCancel.setOnClickListener(view1 -> bottomSheetDialog.dismiss());

                btnConfirm.setOnClickListener(view12 -> {
                    int checkedId = rgReasons.getCheckedRadioButtonId();
                    if (checkedId != -1) {
                        RadioButton selected = view.findViewById(checkedId);
                        //String reason = selected.getText().toString();

                        boolean success = db.cancelOrderItem(item.getOrderItemID());
                        if (success) {
                            //Toast.makeText(context, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                            item.setStatus("Đã hủy");
                            int position_item_remove = holder.getAdapterPosition();
                            orders.remove(position_item_remove);
                            notifyItemRemoved(position_item_remove);

                            if (orderChangedListener != null) {
                                orderChangedListener.onOrderChanged();
                            }
                            //notifyItemChanged(holder.getAdapterPosition());
                        } else {
                            Toast.makeText(context, "Không thể hủy đơn này", Toast.LENGTH_SHORT).show();
                        }
                        bottomSheetDialog.dismiss();
                    } else {
                        Toast.makeText(context, "Vui lòng chọn lý do hủy", Toast.LENGTH_SHORT).show();
                    }
                });
                bottomSheetDialog.show();
            }
        });

        holder.btnMuaLai.setOnClickListener(v->{
            List<CartItem> buyAgainList = new ArrayList<>();
                CartItem cartItem = new CartItem();
                cartItem.setFoodId(item.getFoodID());
                cartItem.setFoodTitle(item.getFoodTitle());
                cartItem.setFoodImagePath(item.getImagePath());
                cartItem.setPricePerItem(item.getPricePerItem());
                cartItem.setQuantity(item.getQuantity());
                cartItem.setNote(item.getNote());

                buyAgainList.add(cartItem);
            Intent intent = new Intent(context, ThanhToanActivity.class);
            intent.putExtra("cart_items", (Serializable) buyAgainList);
            intent.putExtra("from_buy_now", true);
            context.startActivity(intent);
        });

        String status = item.getStatus();
        if (status != null) {
            switch (status) {
                case "Chờ xác nhận":
                    holder.itemView.findViewById(R.id.button3);
                    holder.itemView.findViewById(R.id.button4).setVisibility(View.VISIBLE);
                    break;
                case "Chờ lấy hàng":
                    holder.itemView.findViewById(R.id.button3);
                    holder.itemView.findViewById(R.id.button4).setVisibility(View.VISIBLE);
                    break;
                case "Đang giao":
                    holder.itemView.findViewById(R.id.button3);
                    holder.itemView.findViewById(R.id.button4).setVisibility(View.VISIBLE);
                    break;
                case "Đã giao":
                    holder.itemView.findViewById(R.id.button3).setVisibility(View.VISIBLE);
                    holder.itemView.findViewById(R.id.button4);
                    break;
                case "Đã hủy":
                    holder.itemView.findViewById(R.id.button3).setVisibility(View.VISIBLE);
                    holder.itemView.findViewById(R.id.button4);
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderName, txtOrderStatus, total_quantity_text_view,total_price_text_view;
        ImageView imgOrder;
        Button btnHuyDon,btnMuaLai;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderName = itemView.findViewById(R.id.product_name_text_view);
            txtOrderStatus = itemView.findViewById(R.id.tvTrangthai);
            total_quantity_text_view = itemView.findViewById(R.id.total_quantity_text_view);
            total_price_text_view = itemView.findViewById(R.id.total_price_text_view);
            imgOrder = itemView.findViewById(R.id.product_image_view);

            btnMuaLai = itemView.findViewById(R.id.button3);
            btnHuyDon = itemView.findViewById(R.id.button4);
        }
    }
    public void setData(List<OrderItemDisplay> newData) {
        this.orders.clear();
        this.orders.addAll(newData);
        notifyDataSetChanged();
    }

}


/* Phát triển thêm tính năng thêm ảnh
* String imagePath = item.getImagePath();

if (imagePath != null) {
    if (imagePath.startsWith("file://") || imagePath.startsWith("content://")) {
        // Ảnh từ thư viện
        Glide.with(holder.itemView.getContext())
             .load(Uri.parse(imagePath))
             .placeholder(R.drawable.loading)
             .error(R.drawable.no_image)
             .into(holder.imgOrder);
    } else {
        // Ảnh từ drawable
        int resId = holder.itemView.getContext()
                     .getResources()
                     .getIdentifier(imagePath, "drawable",
                            holder.itemView.getContext().getPackageName());

        if (resId != 0) {
            Glide.with(holder.itemView.getContext())
                 .load(resId)
                 .placeholder(R.drawable.loading)
                 .error(R.drawable.no_image)
                 .into(holder.imgOrder);
        } else {
            // Ảnh không tồn tại trong drawable
            holder.imgOrder.setImageResource(R.drawable.no_image);
        }
    }
} else {
    holder.imgOrder.setImageResource(R.drawable.no_image);
}
*/