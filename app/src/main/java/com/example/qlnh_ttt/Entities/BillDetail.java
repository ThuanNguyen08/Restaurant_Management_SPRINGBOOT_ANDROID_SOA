package com.example.qlnh_ttt.Entities;

public class BillDetail {
    private Integer billDetailID;
    private Integer billID;
    private Integer foodID;
    private Integer quantity;
    private Integer price;

    public BillDetail() {}

    public Integer getBillDetailID() {
        return billDetailID;
    }

    public void setBillDetailID(Integer billDetailID) {
        this.billDetailID = billDetailID;
    }

    public Integer getBillID() {
        return billID;
    }

    public void setBillID(Integer billID) {
        this.billID = billID;
    }

    public Integer getFoodID() {
        return foodID;
    }

    public void setFoodID(Integer foodID) {
        this.foodID = foodID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}
