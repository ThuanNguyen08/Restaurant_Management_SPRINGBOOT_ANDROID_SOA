package com.example.tbfood.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tbfood.entities.Food;

@Repository
public interface FoodRepository  extends JpaRepository<Food, Integer>{
	Optional<Food> findByFoodID(int id);
}
