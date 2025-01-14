package com.example.FoodAndDmFood.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class Food {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int foodID;

	@Column(nullable = false, columnDefinition = "varchar(50)" )
	private String foodName;
	
	@ManyToOne
    @JoinColumn(name = "dm_food_id", referencedColumnName = "dm_food_id", nullable = false)
    private DmFood dmFood;  // Thay đổi từ int sang DmFood
    
	
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
        return dmFood != null ? dmFood.getDmFoodId() : 0;
    }

    public void setDmFoodID(int dmFoodId) {
        if (this.dmFood == null) {
            this.dmFood = new DmFood();
        }
        this.dmFood.setDmFoodId(dmFoodId);
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
