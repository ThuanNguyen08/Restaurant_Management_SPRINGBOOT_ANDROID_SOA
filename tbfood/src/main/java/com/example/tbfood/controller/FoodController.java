package com.example.tbfood.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.tbfood.entities.Food;
import com.example.tbfood.service.FoodService;
import com.example.tbfood.service.RequestOtherPortService;

@RestController
@RequestMapping("/api/v1/food")
public class FoodController {
	
	@Autowired
	private FoodService service;
	
	@Autowired
	private RequestOtherPortService RequestOtherPortService;
	
	public void Authentication(String token) {
		if (!RequestOtherPortService.auth(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
		}
	}

	@GetMapping
	public List<Food> getAll(@RequestHeader("Authorization") String token) {
		Authentication(token);
		return service.getAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@RequestHeader("Authorization") String token  , @PathVariable int id){
		try {
			Authentication(token);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(service.findById(id));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Có lỗi xảy ra rồi ");
		}
	}

	@PostMapping("/add")
    public ResponseEntity<?> addFood(@RequestHeader("Authorization") String token, @RequestBody Food food) {
        try {
            Authentication(token);
            String categoryName = RequestOtherPortService.getById(token, food.getDmFoodID());
            
            if(categoryName != null && categoryName.length() > 0) {
                service.addFood(food);
                return ResponseEntity.ok("Thêm thành công món ăn: " + food.getFoodName());
            } else {
                return ResponseEntity.badRequest().body("Danh mục chưa tồn tại");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }
}
