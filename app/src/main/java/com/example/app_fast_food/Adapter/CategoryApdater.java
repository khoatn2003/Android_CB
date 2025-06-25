package com.example.app_fast_food.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app_fast_food.Activity.ListFoodActivity;
import com.example.app_fast_food.Model.Category;
import com.example.app_fast_food.R;
import com.example.app_fast_food.databinding.ViewholderCategoryBinding;

import java.util.List;

public class CategoryApdater extends RecyclerView.Adapter<CategoryApdater.CategoryViewHolder> {

    private List<Category> categoryList;
    private Context context;
    public CategoryApdater(List<Category> categoryList, Context context){
        this.categoryList = categoryList;
        this.context = context;
    }


    @NonNull
    @Override
    public CategoryApdater.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryApdater.CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.binding.catNameTxt.setText(category.getName());
        //Log.d("CategoryAdapterName", "onBindViewHolder: " + category.getName());
        // Gán background linh hoạt dựa trên ID
        /*
        int bgResId = context.getResources().getIdentifier(
                "cat_" + category.getId() + "_background", "drawable", context.getPackageName());
        if (bgResId != 0) {
            holder.binding.imgCat.setBackgroundResource(bgResId);
        }*/
        // Load ảnh theo tên file từ ImagePath
        int drawableResourceId = context.getResources().getIdentifier(
                category.getImagePath(), "drawable", context.getPackageName());
        Log.d("AdapterDebug", "CategoryID: " + category.getId()+
                ", Category: " + category.getName() +
                ", ImagePath: " + category.getImagePath() +
                ", drawableResourceId: " + drawableResourceId);
        if (drawableResourceId != 0) {
            holder.binding.imgCat.setImageResource(drawableResourceId);
        } else {
            holder.binding.imgCat.setImageResource(R.drawable.jollibear_logo);
        }

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context,ListFoodActivity.class);
            intent.putExtra("categoryID", category.getId());
            intent.putExtra("categoryName", category.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
      ViewholderCategoryBinding binding;

        public CategoryViewHolder(@NonNull ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding =binding;
        }
    }


}