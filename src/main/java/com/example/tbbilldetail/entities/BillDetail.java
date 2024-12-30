package com.example.tbbilldetail.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbbildetail")

public class BillDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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