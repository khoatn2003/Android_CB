package com.example.app_fast_food.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_fast_food.Cart.CartItem;
import com.example.app_fast_food.R;

import java.util.List;

public class ThanhToanAdapter extends RecyclerView.Adapter<ThanhToanAdapter.ViewHolder> {
    private List<CartItem> cartItemList;
    private Context context;
    private OnEditItemClickListener listener;

    public interface OnEditItemClickListener{
        void OnEditItemClick(int position);
    }
    public void setOnEditItemClickListener(OnEditItemClickListener listener) {
        this.listener = listener;
    }
    public ThanhToanAdapter(List<CartItem> cartItemList, Context context) {
        this.cartItemList = cartItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_sp_thanhtoan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        holder.titleTxt.setText(item.getFoodTitle());
        holder.quantityTxt.setText("x" + item.getQuantity());
        holder.totalTxt.setText(dinhDangTien.dinhdang(item.getToTalPrice()));

        if(item.getFoodImagePath() != null && !item.getFoodImagePath().isEmpty()) {
            int resId = context.getResources().getIdentifier(item.getFoodImagePath(), "drawable", context.getPackageName());
            holder.img_products.setImageResource(resId != 0 ? resId : R.drawable.jollibear_logo);
        }else{
            Log.e("AdapterError", "Tên ảnh rỗng hoặc null cho sản phẩm: " + item.getFoodTitle());
            holder.img_products.setImageResource(R.drawable.jollibear_logo);
        }
        holder.txtEdit.setOnClickListener(v->{
            if(listener!=null){
                listener.OnEditItemClick(position);
            }
        });

        if (item.getNote() != null && !item.getNote().trim().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText("Ghi chú: " + item.getNote());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

    }

    // Trong ThanhToanAdapter
    public void updateData(List<CartItem> newList) {
        this.cartItemList.clear();
        if (newList != null) {
            this.cartItemList.addAll(newList);
        }
        notifyDataSetChanged(); // Cập nhật lại giao diện
    }


    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, quantityTxt, totalTxt,tvNote;
        ImageView img_products;
        TextView txtEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.name_products);
            quantityTxt = itemView.findViewById(R.id.tv_quantity);
            totalTxt = itemView.findViewById(R.id.tv_price);
            img_products =itemView.findViewById(R.id.img_products);

            txtEdit = itemView.findViewById(R.id.txtEditItem);
            tvNote = itemView.findViewById(R.id.tv_note);
        }
    }


}
