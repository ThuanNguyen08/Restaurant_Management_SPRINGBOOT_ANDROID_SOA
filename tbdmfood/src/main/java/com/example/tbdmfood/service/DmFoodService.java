package com.example.tbdmfood.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tbdmfood.entities.DmFood;
import com.example.tbdmfood.repository.DmFoodRepository;

@Service
public class DmFoodService {

	@Autowired
	private DmFoodRepository repo;
	
	public boolean add(DmFood dmFood) {
		if (repo.existsByCategoryName(dmFood.getCategoryName())) {
	        return false; 
	    }
	    DmFood dmFoods = new DmFood();
	    dmFoods.setCategoryName(dmFood.getCategoryName());
	    repo.save(dmFoods);
	    return true; 
	}
	
	public List<DmFood> getAll(){
		return repo.findAll();
	}
	
	public String getById(int id) {
		Optional<DmFood> dmFood = repo.findByDmFoodId(id);
		return dmFood.get().getCategoryName();
	}
	
	public boolean deleteById(int id) {
		boolean exist = repo.existsByDmFoodId(id);
		if(exist) {
			repo.deleteById(id); 
			return true;
		}
		return false;
	}
}
