package com.example.app_fast_food.Model;

public class Category {
    private int id;
    private String imagePath;
    private String name;
    public Category() {
    }

    public Category(int id, String imagePath, String name) {
        this.id = id;
        this.imagePath = imagePath;
        this.name = name;
    }

    // Getters
    public int getId() { return id; }
    public String getImagePath() { return imagePath; }
    public String getName() { return name; }
    public void setId(int id) { this.id = id; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setName(String name) { this.name = name; }


    @Override
    public String toString() {
        return "Category{" +
                "id=" + id+
                ", imagePath='" + imagePath + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}

