package com.example.tbfood.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.tbfood.entities.Food;
import com.example.tbfood.repository.FoodRepository;

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
}
