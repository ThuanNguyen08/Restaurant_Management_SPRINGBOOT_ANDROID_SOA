package com.example.qlnh_ttt.Entities;


public class Food {
    private int foodID;
    private String foodName;
    private int dmFoodID;
    private String price;
    private byte[] avtFood;

    public Food() {
    }

    public Food(int foodID, String foodName, int dmFoodID, String price, byte[] avtFood) {
        this.foodID = foodID;
        this.foodName = foodName;
        this.dmFoodID = dmFoodID;
        this.price = price;
        this.avtFood = avtFood;
    }

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getDmFoodID() {
        return dmFoodID;
    }

    public void setDmFoodID(int dmFoodID) {
        this.dmFoodID = dmFoodID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public byte[] getAvtFood() {
        return avtFood;
    }

    public void setAvtFood(byte[] avtFood) {
        this.avtFood = avtFood;
    }

    @Override
    public String toString() {
        return "Food{" +
                "foodID=" + foodID +
                ", foodName='" + foodName + '\'' +
                ", dmFoodID=" + dmFoodID +
                ", price='" + price + '\'' +
                '}';
    }
}