package com.example.FoodAndDmFood.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.FoodAndDmFood.entities.DmFood;

@Repository
public interface DmFoodRepository extends JpaRepository<DmFood, Integer>{
	boolean existsByCategoryName(String categoryName);
	boolean existsByDmFoodId(int dmFoodId);
	Optional<DmFood> findByDmFoodId(int dmFoodId);
}
