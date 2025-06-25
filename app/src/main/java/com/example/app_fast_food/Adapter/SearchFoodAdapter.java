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
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.R;

import java.util.List;
import java.util.Locale;

public class SearchFoodAdapter extends RecyclerView.Adapter<SearchFoodAdapter.SearchFoodViewHolder> {
    private Context context;
    private List<Foods> foodList;

    public SearchFoodAdapter(Context context, List<Foods> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    public void setData(List<Foods> newList) {
        this.foodList = newList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SearchFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_search_food, parent, false);
        return new SearchFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFoodViewHolder holder, int position) {
        Foods food = foodList.get(position);
        if (food == null) return;

        holder.txtTitle.setText(food.getTitle());
        holder.timeTxt.setText(String.valueOf(food.getTimeValue()) + " min");
        holder.txtPrice.setText(dinhDangTien.dinhdang(food.getPrice()));
        holder.rateTxt.setText(String.valueOf(food.getStar()));

        // Load ảnh từ imagePath (có thể là drawable name hoặc asset path)
        Glide.with(context)
                .load(getImageResource(food.getImagePath()))
                .placeholder(R.drawable.jollibear_logo)
                .into(holder.imgFood);

        // Xử lý khi click vào món ăn (có thể chuyển sang chi tiết món ăn)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailFoodActivity.class);
            intent.putExtra("object", food); // Foods implements Serializable
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return foodList != null ? foodList.size() : 0;
    }

    public class SearchFoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView txtTitle, txtPrice, timeTxt,rateTxt;

        public SearchFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            rateTxt = itemView.findViewById(R.id.rateTxt);
        }
    }
    private int getImageResource(String imageName) {
        if (imageName == null || imageName.isEmpty()) return R.drawable.jollibear_logo;
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }
}
