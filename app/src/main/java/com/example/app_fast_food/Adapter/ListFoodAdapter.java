package com.example.app_fast_food.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_fast_food.Activity.DetailFoodActivity;
import com.example.app_fast_food.Activity.EditFoodActivity;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.R;

import java.util.List;

public class ListFoodAdapter extends RecyclerView.Adapter<ListFoodAdapter.ListFoodViewHolder> {
    private Context context;
    private List<Foods> foodList;
    // private DecimalFormat formatter = new DecimalFormat("#,###"); // Nếu bạn muốn định dạng giá riêng

    public ListFoodAdapter(Context context, List<Foods> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ListFoodAdapter.ListFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_list_food,parent,false);
        return new ListFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListFoodAdapter.ListFoodViewHolder holder, int position) {
        Foods food = foodList.get(position);
        holder.titleTxt.setText(food.getTitle());
        holder.titleTxt.setText(food.getTitle());
        // Định dạng giá tiền (bạn có thể có một helper class cho việc này)
        holder.priceTxt.setText(dinhDangTien.dinhdang(food.getPrice()));
        holder.starTxt.setText(String.valueOf(food.getStar()));
        holder.timeTxt.setText(String.valueOf(food.getTimeValue()) + " min");

        // Load ảnh
        int imageResourceId = context.getResources().getIdentifier(
                food.getImagePath(),
                "drawable",
                context.getPackageName()
        );

        if (imageResourceId != 0) {
            holder.pic.setImageResource(imageResourceId);
        } else {
            holder.pic.setImageResource(R.drawable.jollibear_logo);
        }

        // Xử lý sự kiện click cho toàn bộ item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailFoodActivity.class);
            intent.putExtra("object", food); // Gửi toàn bộ đối tượng Foods (đã implement Serializable)
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
    // Phương thức để cập nhật dữ liệu

    public static class ListFoodViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleTxt, priceTxt, starTxt, timeTxt;

        public ListFoodViewHolder(@NonNull View itemView) {
            super(itemView);

            pic = itemView.findViewById(R.id.img);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            starTxt = itemView.findViewById(R.id.rateTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
        }
    }

    public void updateData(List<Foods> newFoodList) {
        this.foodList.clear();
        this.foodList.addAll(newFoodList);
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật lại giao diện
    }
}