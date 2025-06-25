package com.example.app_fast_food.Model;

import java.io.Serializable;

public class Foods implements Serializable {
    private int Id;                     // Tương ứng với COLUMN_FOOD_ID
    private String Title;               // Tương ứng với COLUMN_FOOD_TITLE
    private String Description;
    private double Price;
    private String ImagePath;
    private int CategoryId;
    private double Star;
    private int TimeValue;
    private boolean BestFood;


    public Foods() {
    }

    public Foods(int id, String title, String description, double price, String imagePath,
                 int categoryId, double star, int TimeValue, boolean bestFood) {
        this.Id = id;
        this.Title = title;
        this.Description = description;
        this.Price = price;
        this.ImagePath = imagePath;
        this.CategoryId = categoryId;
        this.Star = star;
        this.TimeValue = TimeValue;
        this.BestFood = bestFood;
    }


    // Getters và Setters đầy đủ...

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public double getStar() {
        return Star;
    }

    public void setStar(double star) {
        Star = star;
    }

    public int getTimeValue() {
        return TimeValue;
    }

    public void setTimeValue(int timeValue) {
        TimeValue = timeValue;
    }

    public boolean isBestFood() {
        return BestFood;
    }

    public void setBestFood(boolean bestFood) {
        BestFood = bestFood;
    }
}