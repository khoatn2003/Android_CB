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

import com.bumptech.glide.Glide;
import com.example.app_fast_food.Activity.DetailFoodActivity;
import com.example.app_fast_food.Activity.EditFoodActivity;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.R;

import java.util.List;
import java.util.Locale;

public class BestFoodAdapter extends RecyclerView.Adapter<BestFoodAdapter.BestFoodViewHolder> {

    private Context context;
    private List<Foods> bestFoodList;

    public BestFoodAdapter(Context context, List<Foods> bestFoodList) {
        this.context = context;
        this.bestFoodList = bestFoodList;
    }
    @NonNull
    @Override
    public BestFoodAdapter.BestFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_best_food, parent, false);
        return new BestFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestFoodAdapter.BestFoodViewHolder holder, int position) {
        Foods food = bestFoodList.get(position);

        holder.titleTxt.setText(food.getTitle());
        // Định dạng giá tiền có dấu phẩy ngăn cách hàng nghìn và thêm "đ"
        holder.priceTxt.setText(String.format(Locale.GERMAN, "%,.0f đ", food.getPrice()));
        holder.starTxt.setText(String.valueOf(food.getStar()));
        holder.timeTxt.setText(String.valueOf(food.getTimeValue()) + " min");
        // Load ảnh (giả định imagePath là tên file không có đuôi trong drawable)
        int imageResourceId = context.getResources().getIdentifier(
                food.getImagePath(),
                "drawable",
                context.getPackageName()
        );

        if (imageResourceId != 0) {
            Glide.with(context)
                    .load(imageResourceId)
                    .placeholder(R.drawable.jollibear_logo)
                    .into(holder.pic);
        } else {
            // Nếu không tìm thấy ảnh, đặt ảnh mặc định hoặc ẩn ImageView
            Glide.with(context)
                    .load(R.drawable.jollibear_logo) // Ảnh placeholder mặc định
                    .into(holder.pic);
        }

        holder.plusBtn.setOnClickListener(v -> {});
        holder.itemView.setOnClickListener(v->{
            Intent intent=new Intent(context, DetailFoodActivity.class);
            intent.putExtra("object",food);
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return bestFoodList.size();
    }


    public static class BestFoodViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleTxt, priceTxt, starTxt, timeTxt;
        TextView plusBtn; // TextView15 đóng vai trò là nút "+"

        public BestFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            starTxt = itemView.findViewById(R.id.starTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            plusBtn = itemView.findViewById(R.id.textView15); // ID của nút "+"
            // Không cần findViewById cho imageView6 và imageView7 nếu chúng chỉ là icon tĩnh
        }

    }
    // Phương thức để cập nhật dữ liệu nếu cần (ví dụ sau khi refresh)
    public void updateData(List<Foods> newBestFoodList) {
        this.bestFoodList.clear();
        this.bestFoodList.addAll(newBestFoodList);
        notifyDataSetChanged();
    }
}
