package com.example.tbdmfood.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class DmFood {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int dmFoodId;

	@Column(nullable = false, columnDefinition = "varchar(50)" )
	private String categoryName;
	
	public DmFood() {}

	public int getDmFoodId() {
		return dmFoodId;
	}

	public void setDmFoodId(int dmFoodId) {
		this.dmFoodId = dmFoodId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	

}
