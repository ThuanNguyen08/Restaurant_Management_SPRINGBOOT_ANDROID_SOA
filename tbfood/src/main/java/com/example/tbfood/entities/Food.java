package com.example.tbfood.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Food {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int foodID;

	@Column(nullable = false, columnDefinition = "varchar(50)" )
	private String foodName;
	
	@Column(nullable = false, columnDefinition = "INT" )
	private int dmFoodID;
	
	@Column(nullable = false, columnDefinition = "varchar(50)" )
	private String price;
	
	@Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] avtFood;
	
	public Food() {}

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

	
	
	
}
