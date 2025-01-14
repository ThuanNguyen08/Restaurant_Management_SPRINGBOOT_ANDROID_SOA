package com.example.BillAndDetailBil.entities;


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
 // Thay billID thành relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "billID", referencedColumnName = "billID")
    private Bill bill;	
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
	        return bill != null ? bill.getBillID() : null;
	    }

	    public void setBillID(Integer billID) {
	        if (this.bill == null) {
	            this.bill = new Bill();
	        }
	        this.bill.setBillID(billID);
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