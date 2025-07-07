package com.example.app_fast_food.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.app_fast_food.Activity.SearchOrderActivity;
import com.example.app_fast_food.Helper.OrderDatabase;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.Order.OrderItemDisplay;
import com.example.app_fast_food.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ConcurrentModificationException;
import java.util.List;

public class SearchOrderAdapter extends RecyclerView.Adapter<SearchOrderAdapter.SearchOrderViewHolder> {
    private Context context;
    private List<OrderItemDisplay> orderItemDisplayList;
    private  final OrderDatabase db;

    public SearchOrderAdapter(Context context, List<OrderItemDisplay> orderItemDisplayList) {
        this.context = context;
        this.orderItemDisplayList = orderItemDisplayList;
        this.db = new OrderDatabase(context);
    }

    public void setData(List<OrderItemDisplay> newList) {
        this.orderItemDisplayList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchOrderAdapter.SearchOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new SearchOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchOrderAdapter.SearchOrderViewHolder holder, int position) {
        OrderItemDisplay itemDisplay = orderItemDisplayList.get(position);

        holder.txtOrderName.setText(itemDisplay.getFoodTitle());
        holder.txtOrderStatus.setText(itemDisplay.getStatus());
        holder.total_quantity_text_view.setText(context.getString(R.string.product_quantity, itemDisplay.getQuantity()));
        holder.total_price_text_view.setText(dinhDangTien.dinhdang(itemDisplay.getQuantity() * itemDisplay.getPricePerItem()));

        int imageResId = context.getResources().getIdentifier(
                itemDisplay.getImagePath(), "drawable", context.getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(imageResId)
                .placeholder(R.drawable.jollibear_logo)
                .error(R.drawable.jollibear_logo)
                .into(holder.imgOrder);

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderItem", itemDisplay);
            intent.putExtra("current_status", itemDisplay.getStatus());
            ((Activity) context).startActivityForResult(intent, SearchOrderActivity.REQUEST_ORDER_SEARCH);
        });
      holder.btnHuyDon.setVisibility(View.GONE);
      holder.btnMuaLai.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return orderItemDisplayList.size();
    }

    public class SearchOrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderName, txtOrderStatus, total_quantity_text_view,total_price_text_view;
        ImageView imgOrder;
        Button btnHuyDon,btnMuaLai;
        public SearchOrderViewHolder(@NonNull View itemView) {
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
}
