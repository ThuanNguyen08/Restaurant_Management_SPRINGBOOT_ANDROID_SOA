package com.example.tbdmfood.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tbdmfood.entities.DmFood;

@Repository
public interface DmFoodRepository extends JpaRepository<DmFood, Integer>{
	boolean existsByCategoryName(String categoryName);
	boolean existsByDmFoodId(int dmFoodId);
	Optional<DmFood> findByDmFoodId(int dmFoodId);
}
