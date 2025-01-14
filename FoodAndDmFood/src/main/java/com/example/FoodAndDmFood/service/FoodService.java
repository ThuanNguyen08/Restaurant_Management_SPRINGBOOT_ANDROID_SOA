package com.example.FoodAndDmFood.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.FoodAndDmFood.entities.Food;
import com.example.FoodAndDmFood.repository.FoodRepository;

@Service
public class FoodService {

	@Autowired
	private FoodRepository repo;
	
	public List<Food> getAll(){
		return repo.findAll();
	}
	
	public Food findById(int id) {
		Optional<Food> existfood = repo.findByFoodID(id);
		if(existfood.isPresent()) {
			return existfood.get();
		}
		else {
			throw new RuntimeException("không tìm thấy");
		}
	}
	
	public void addFood(Food food) {
		Optional<Food> existfood = repo.findByFoodID(food.getFoodID());
		Food foods = new Food();
		if(existfood.isPresent()) {
			foods.setFoodID(food.getFoodID());
			foods.setFoodName(food.getFoodName());
			foods.setDmFoodID(food.getDmFoodID());
			foods.setPrice(food.getPrice());
			foods.setAvtFood(food.getAvtFood());
		}
		else {
			foods.setFoodName(food.getFoodName());
			foods.setDmFoodID(food.getDmFoodID());
			foods.setPrice(food.getPrice());
			foods.setAvtFood(food.getAvtFood());
		}
		
		repo.save(foods);
	}
	
	public List<Food> getFoodsByCategory(int dmFoodID) {
	    return repo.findByDmFood_DmFoodId(dmFoodID);
	}
	
	public Food updateFood(Food food) {

        return repo.save(food);
    }
	
	public boolean deleteFood(int foodID) {
        if (repo.existsById(foodID)) {
            repo.deleteById(foodID);
            return true;
        }
        return false;
    }
	public void deleteAllByDmFoodid(int id) {
		List<Food> list = repo.findByDmFood_DmFoodId(id);
		repo.deleteAll(list);
		
	}
	
}